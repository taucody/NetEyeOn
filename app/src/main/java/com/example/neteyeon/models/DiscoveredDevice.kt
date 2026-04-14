package com.example.neteyeon.models

data class DiscoveredDevice(
    val ip: String,
    val hostname: String? = null,
    val macAddress: String? = null,
    val vendor: String? = null,
    val openPorts: List<Int> = emptyList(),
    val portBanners: Map<Int, String> = emptyMap(),   // port
    val isReachable: Boolean = false,
    val latencyMs: Long? = null,
    val osGuess: String? = null,                       // estimé via TTL
    val securityFlags: List<SecurityFlag> = emptyList()
)

enum class SecurityFlag(val label: String, val severity: Severity) {
    TELNET_OPEN("Telnet ouvert (port 23)", Severity.CRITICAL),
    FTP_OPEN("FTP ouvert (port 21)", Severity.HIGH),
    RDP_OPEN("RDP exposé (port 3389)", Severity.HIGH),
    HTTP_NO_HTTPS("HTTP sans HTTPS", Severity.MEDIUM),
    SSH_OPEN("SSH exposé (port 22)", Severity.LOW),
    MANY_PORTS_OPEN("Beaucoup de ports ouverts", Severity.MEDIUM),
    UNKNOWN_DEVICE("Appareil inconnu (fabricant non identifié)", Severity.LOW),
    OLD_SERVICE_BANNER("Bannière de service potentiellement obsolète", Severity.MEDIUM)
}

enum class Severity { CRITICAL, HIGH, MEDIUM, LOW }