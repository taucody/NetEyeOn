package com.example.neteyeon

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import com.example.neteyeon.screens.AllowingScreen
import com.example.neteyeon.screens.CGUScreen
import com.example.neteyeon.screens.OnboardingScreen
import com.example.neteyeon.screens.WifiScanScreen
import com.example.neteyeon.screens.ScanResultsScreen
import com.example.neteyeon.screens.DeviceDetailsScreen
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.neteyeon.Screen
import com.example.neteyeon.screens.NetworkScanningScreen
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.network.NetworkSecurityReport

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var scanResults by remember { mutableStateOf<List<DiscoveredDevice>>(emptyList()) }
    var securityReport by remember { mutableStateOf<NetworkSecurityReport?>(null) }

    Surface(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onContinueClicked = {
                        navController.navigate(Screen.Cgu.route)
                    }
                )
            }

            composable(Screen.Cgu.route) {
                CGUScreen(
                    onContinueClicked = {
                        navController.navigate(Screen.Allowing.route)
                    }
                )
            }

            composable(Screen.Allowing.route) {
                AllowingScreen(
                    onContinueClicked = {
                        navController.navigate(Screen.Scanning.route)
                    }
                )
            }

            composable(Screen.Scanning.route) {
                WifiScanScreen(
                    onContinueClicked = { ipRange ->
                        navController.navigate(Screen.NetworkScanning.createRoute(ipRange))
                    }
                )
            }

            composable(
                route = Screen.NetworkScanning.route,
                arguments = listOf(navArgument("ipRange") { type = NavType.StringType })
            ) { backStackEntry ->
                val ipRange = backStackEntry.arguments?.getString("ipRange") ?: ""
                NetworkScanningScreen(
                    ipRange = ipRange,
                    onScanFinished = { devices, report ->
                        scanResults = devices
                        securityReport = report
                        navController.navigate("results")
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }

            composable(Screen.ScanResults.route) {
                ScanResultsScreen(
                    devices = scanResults,
                    report = securityReport,
                    onBackClicked = {
                        navController.popBackStack(Screen.Scanning.route, inclusive = false)
                    },
                    onDeviceClicked = { device ->
                        selectedDevice = device
                        navController.navigate(Screen.DeviceDetails.route)
                    }
                )
            }

            composable(Screen.DeviceDetails.route) {
                selectedDevice?.let { device ->
                    DeviceDetailsScreen(
                        device = device,
                        onBackClicked = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MyAppPreview() {
    NetEyeOnTheme {
        MyApp(Modifier.fillMaxSize())
    }
}
