package com.example.neteyeon.models

data class DiscoveredDevice(
    val ip: String,
    val hostname: String? = null,
    val macAddress: String? = null,
    val vendor: String? = null,
    val openPorts: List<Int> = emptyList(),
    val isReachable: Boolean = false
)
