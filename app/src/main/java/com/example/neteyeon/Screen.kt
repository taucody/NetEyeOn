package com.example.neteyeon

import android.net.Uri

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Cgu : Screen("cgu")
    object Allowing : Screen("allowing")
    object Scanning : Screen("scanning")
    object History : Screen("history")
    object NetworkScanning : Screen("network_scanning/{ipRange}/{networkName}") {
        fun createRoute(ipRange: String, networkName: String) = 
            "network_scanning/${Uri.encode(ipRange)}/${Uri.encode(networkName)}"
    }
    object ScanResults : Screen("results")
    object DeviceDetails : Screen("device_details")

    object ExportReport : Screen("export_report/{historyId}") {
        fun createRoute(historyId: String) = "export_report/$historyId"
    }
}
