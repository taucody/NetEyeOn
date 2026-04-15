package com.example.neteyeon.models

import com.example.neteyeon.network.NetworkSecurityReport
import java.util.Date

data class ScanHistoryItem(
    val id: String,
    val date: Date,
    val networkName: String,
    val devices: List<DiscoveredDevice>,
    val report: NetworkSecurityReport
)
