package com.example.neteyeon

import android.net.Uri

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Cgu : Screen("cgu")
    object Allowing : Screen("allowing")
    object Scanning : Screen("scanning")
    object NetworkScanning : Screen("network_scanning/{ipRange}") {
        fun createRoute(ipRange: String) = "network_scanning/${Uri.encode(ipRange)}"
    }
    object ScanResults : Screen("scan_results")
    object DeviceDetails : Screen("device_details")
}
