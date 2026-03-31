package com.example.neteyeon

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Cgu : Screen("cgu")
    object Allowing : Screen("allowing")
    object Scanning : Screen("scanning")
    object ScanResults : Screen("scan_results")
    object DeviceDetails : Screen("device_details")
}
