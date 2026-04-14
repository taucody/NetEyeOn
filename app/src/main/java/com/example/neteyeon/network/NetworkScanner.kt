package com.example.neteyeon.network

import android.util.Log
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.models.SecurityFlag
import com.example.neteyeon.models.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class NetworkScanner {

    // Ports à scanner selon profil
    private val basicPorts    = listOf(80, 443, 22, 23)
    private val advancedPorts = listOf(21, 22, 23, 25, 53, 80, 110, 143, 443, 445, 3389, 8080, 8443)
    private val customPorts   = listOf(
        21, 22, 23, 25, 53, 80, 110, 143, 443, 445,
        3306, 3389, 5900, 6379, 8080, 8443, 27017
    )

    suspend fun scanRange(
        ipRange: String,
        scanProfile: String = "Basique",
        onProgress: (Int, Int) -> Unit
    ): List<DiscoveredDevice> = withContext(Dispatchers.IO) {

        val ips = parseIpRange(ipRange)
        val discoveredDevices = mutableListOf<DiscoveredDevice>()
        val total = ips.size
        var current = 0

        // Charge le cache ARP une seule fois pour tout le scan
        val arpCache = readArpCache()

        for (chunk in ips.chunked(20)) {
            val deferreds = chunk.map { ip ->
                async {
                    val device = scanIp(ip, scanProfile, arpCache)
                    synchronized(discoveredDevices) {
                        if (device != null) discoveredDevices.add(device)
                        current++
                        onProgress(current, total)
                    }
                }
            }
            deferreds.awaitAll()
        }

        discoveredDevices
    }

    // Scan d'une IP
    private fun scanIp(
        ip: String,
        scanProfile: String,
        arpCache: Map<String, String>
    ): DiscoveredDevice? {
        return try {
            val start = System.currentTimeMillis()
            val address = InetAddress.getByName(ip)
            if (!address.isReachable(1000)) return null

            val latency = System.currentTimeMillis() - start
            val hostname = resolveHostname(address, ip)
            val macAddress = arpCache[ip]
            val vendor = macAddress?.let { lookupVendor(it) }

            val portsToScan = when (scanProfile) {
                "Avance" -> advancedPorts
                "Custom" -> customPorts
                else     -> basicPorts
            }

            val openPorts = scanPorts(ip, portsToScan)
            val banners   = grabBanners(ip, openPorts)
            val osGuess   = guessOsFromTtl(ip)
            val flags     = analyzeSecurityFlags(openPorts, banners, vendor)

            Log.d("NetworkScanner", "Found: $ip ($hostname) MAC=$macAddress Vendor=$vendor Ports=$openPorts")

            DiscoveredDevice(
                ip            = ip,
                hostname      = hostname,
                macAddress    = macAddress,
                vendor        = vendor,
                openPorts     = openPorts,
                portBanners   = banners,
                isReachable   = true,
                latencyMs     = latency,
                osGuess       = osGuess,
                securityFlags = flags
            )
        } catch (e: Exception) {
            Log.e("NetworkScanner", "Error scanning $ip: ${e.message}")
            null
        }
    }

    // Hostname – évite de retourner l'IP elle-même
    private fun resolveHostname(address: InetAddress, ip: String): String? {
        val hostname = address.canonicalHostName
        return if (hostname != ip) hostname else null
    }

    // Lecture du cache ARP : /proc/net/arp  (IP -> MAC), fonctionne sans root pour les IPs que Android a déjà contactées
    private fun readArpCache(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            val lines = java.io.File("/proc/net/arp").readLines()
            // Format : IP address  HW type  Flags  HW address  Mask  Device
            for (line in lines.drop(1)) {
                val cols = line.trim().split("\\s+".toRegex())
                if (cols.size >= 4) {
                    val ip  = cols[0]
                    val mac = cols[3]
                    // "00:00:00:00:00:00" = pas encore résolu, on ignore
                    if (mac != "00:00:00:00:00:00") result[ip] = mac.uppercase()
                }
            }
        } catch (e: Exception) {
            Log.w("NetworkScanner", "ARP cache unavailable: ${e.message}")
        }
        return result
    }

    // OUI Lookup : les 3 premiers octets de la MAC identifient le fabricant
    private val ouiDatabase = mapOf(
        "00:50:56" to "VMware",
        "00:0C:29" to "VMware",
        "B8:27:EB" to "Raspberry Pi Foundation",
        "DC:A6:32" to "Raspberry Pi Foundation",
        "E4:5F:01" to "Raspberry Pi Foundation",
        "18:FE:34" to "Espressif (ESP8266/ESP32)",
        "AC:67:B2" to "Espressif",
        "A4:CF:12" to "Espressif",
        "00:1A:11" to "Google (Nest/Chromecast)",
        "54:60:09" to "Google",
        "F4:F5:D8" to "Google",
        "BC:D0:74" to "Apple",
        "3C:22:FB" to "Apple",
        "A8:BE:27" to "Apple",
        "FC:FB:FB" to "Synology",
        "00:11:32" to "Synology",
        "00:1B:21" to "Intel",
        "8C:8D:28" to "Intel",
        "00:E0:4C" to "Realtek",
        "28:D2:44" to "NETGEAR",
        "A0:40:A0" to "NETGEAR",
        "C4:04:15" to "TP-Link",
        "50:C7:BF" to "TP-Link",
        "30:DE:4B" to "TP-Link",
        "00:90:4C" to "Epigram (Broadcom)",
        "74:D0:2B" to "Xiaomi",
        "AC:C1:EE" to "Shenzhen (IoT générique)",
        "00:25:90" to "Super Micro Computer"
    )

    private fun lookupVendor(mac: String): String? {
        val prefix = mac.substring(0, 8).uppercase()  // "AA:BB:CC"
        return ouiDatabase[prefix]
    }

    // Scan de ports TCP
    private fun scanPorts(ip: String, ports: List<Int>): List<Int> {
        val open = mutableListOf<Int>()
        for (port in ports) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(ip, port), 300)
                    open.add(port)
                }
            } catch (_: Exception) {}
        }
        return open
    }

    // Grab de bannières
    private fun grabBanners(ip: String, openPorts: List<Int>): Map<Int, String> {
        val bannerPorts = setOf(21, 22, 23, 25, 80, 8080)   // ports qui envoient une bannière
        val result = mutableMapOf<Int, String>()

        for (port in openPorts.filter { it in bannerPorts }) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(ip, port), 300)
                    socket.soTimeout = 400
                    val reader = BufferedReader(InputStreamReader(socket.inputStream))
                    val banner = reader.readLine()?.trim() ?: continue
                    if (banner.isNotBlank()) result[port] = banner.take(120) // limite la taille
                }
            } catch (_: Exception) {}
        }
        return result
    }

    // Estimation de l'OS via TTL
    // TTL initial Windows ≈ 128, Linux/Android ≈ 64, Cisco ≈ 255
    // Android ne donne pas directement le TTL reçu via InetAddress,
    // on parse le résultat de ping
    private fun guessOsFromTtl(ip: String): String? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("ping", "-c", "1", "-W", "1", ip))
            val output = process.inputStream.bufferedReader().readText()
            val ttlMatch = Regex("ttl=(\\d+)", RegexOption.IGNORE_CASE).find(output)
            val ttl = ttlMatch?.groupValues?.get(1)?.toIntOrNull() ?: return null

            when {
                ttl <= 64  -> "Linux / Android / macOS"
                ttl <= 128 -> "Windows"
                ttl <= 255 -> "Cisco / Routeur"
                else       -> null
            }
        } catch (_: Exception) { null }
    }

    // Analyse de sécurité: liste de SecurityFlag
    private fun analyzeSecurityFlags(
        openPorts: List<Int>,
        banners: Map<Int, String>,
        vendor: String?
    ): List<SecurityFlag> {
        val flags = mutableListOf<SecurityFlag>()

        if (23 in openPorts)  flags += SecurityFlag.TELNET_OPEN
        if (21 in openPorts)  flags += SecurityFlag.FTP_OPEN
        if (3389 in openPorts) flags += SecurityFlag.RDP_OPEN
        if (22 in openPorts)  flags += SecurityFlag.SSH_OPEN
        if (80 in openPorts && 443 !in openPorts) flags += SecurityFlag.HTTP_NO_HTTPS
        if (openPorts.size > 5) flags += SecurityFlag.MANY_PORTS_OPEN
        if (vendor == null)   flags += SecurityFlag.UNKNOWN_DEVICE

        // Détection bannière potentiellement ancienne
        banners.values.forEach { banner ->
            if (banner.contains(Regex("OpenSSH_[1-6]\\.|vsftpd 2\\.|Apache/1\\.|Apache/2\\.2"))) {
                flags += SecurityFlag.OLD_SERVICE_BANNER
            }
        }

        return flags.distinct()
    }

    // Parser de plage IP
    private fun parseIpRange(range: String): List<String> {
        if (range.contains("/")) {
            val prefix = range.split("/")[0].substringBeforeLast(".")
            return (1..254).map { "$prefix.$it" }
        } else if (range.contains("-")) {
            val parts   = range.split("-")
            val startIp = parts[0].trim()
            val endPart = parts[1].trim()
            val prefix  = startIp.substringBeforeLast(".")
            val start   = startIp.substringAfterLast(".").toInt()
            val end     = if (endPart.contains(".")) endPart.substringAfterLast(".").toInt() else endPart.toInt()
            return (start..end).map { "$prefix.$it" }
        }
        return listOf(range)
    }
}