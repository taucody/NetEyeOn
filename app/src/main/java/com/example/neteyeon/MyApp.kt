package com.example.neteyeon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var scanResults by remember { mutableStateOf<List<DiscoveredDevice>>(emptyList()) }
    var securityReport by remember { mutableStateOf<NetworkSecurityReport?>(null) }

    // Fonction pour vérifier si les permissions nécessaires sont déjà accordées
    fun hasRequiredPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

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
                        // Si les permissions sont déjà là, on saute directement au scan Wifi
                        if (hasRequiredPermissions(context)) {
                            navController.navigate(Screen.Scanning.route) {
                                // On enlève l'écran CGU de la pile pour ne pas y revenir via "Back"
                                popUpTo(Screen.Cgu.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Allowing.route)
                        }
                    }
                )
            }

            composable(Screen.Allowing.route) {
                AllowingScreen(
                    onContinueClicked = {
                        navController.navigate(Screen.Scanning.route) {
                            popUpTo(Screen.Allowing.route) { inclusive = true }
                        }
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
