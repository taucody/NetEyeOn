package com.example.neteyeon.network

import android.util.Log
import com.example.neteyeon.models.DiscoveredDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class NetworkScanner {

    suspend fun scanRange(ipRange: String, onProgress: (Int, Int) -> Unit): List<DiscoveredDevice> = withContext(Dispatchers.IO) {
        val ips = parseIpRange(ipRange)
        val discoveredDevices = mutableListOf<DiscoveredDevice>()
        
        val total = ips.size
        var current = 0

        // Use a limited number of concurrent coroutines to avoid overloading
        val chunkedIps = ips.chunked(20) 
        
        for (chunk in chunkedIps) {
            val deferreds = chunk.map { ip ->
                async {
                    val device = scanIp(ip)
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

    private fun scanIp(ip: String): DiscoveredDevice? {
        Log.d("NetworkScanner", "Scanning IP: $ip")
        return try {
            val address = InetAddress.getByName(ip)
            if (address.isReachable(1000)) {
                val hostname = address.canonicalHostName
                // Basic port scan for common ports
                val openPorts = scanCommonPorts(ip)
                Log.d("NetworkScanner", "Found device: $ip ($hostname)")
                DiscoveredDevice(
                    ip = ip,
                    hostname = if (hostname != ip) hostname else null,
                    isReachable = true,
                    openPorts = openPorts
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("NetworkScanner", "Error scanning $ip: ${e.message}")
            null
        }
    }

    private fun scanCommonPorts(ip: String): List<Int> {
        val portsToScan = listOf(21, 22, 23, 80, 443, 8080, 3389)
        val openPorts = mutableListOf<Int>()
        for (port in portsToScan) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 200)
                socket.close()
                openPorts.add(port)
            } catch (e: Exception) {
                // Port closed or filtered
            }
        }
        return openPorts
    }

    private fun parseIpRange(range: String): List<String> {
        // Very basic parser for 192.168.1.0/24 or 192.168.1.0-255
        // For simplicity, if it's 192.168.1.0/24, we return 192.168.1.1 to 192.168.1.254
        if (range.contains("/")) {
            val parts = range.split("/")
            val baseIp = parts[0]
            val prefix = baseIp.substringBeforeLast(".")
            return (1..254).map { "$prefix.$it" }
        } else if (range.contains("-")) {
             val parts = range.split("-")
             val startIp = parts[0].trim()
             val endPart = parts[1].trim()
             val prefix = startIp.substringBeforeLast(".")
             val start = startIp.substringAfterLast(".").toInt()
             val end = if (endPart.contains(".")) endPart.substringAfterLast(".").toInt() else endPart.toInt()
             return (start..end).map { "$prefix.$it" }
        }
        return listOf(range)
    }
}
