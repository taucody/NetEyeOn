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
import com.example.neteyeon.screens.HistoryScreen
import com.example.neteyeon.models.ScanHistoryItem
import java.util.Date
import java.util.UUID

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // États pour le scan en cours
    var selectedDevice by remember { mutableStateOf<DiscoveredDevice?>(null) }
    var scanResults by remember { mutableStateOf<List<DiscoveredDevice>>(emptyList()) }
    var securityReport by remember { mutableStateOf<NetworkSecurityReport?>(null) }
    
    // Historique des scans (mémoire vive pour l'instant)
    val scanHistory = remember { mutableStateListOf<ScanHistoryItem>() }

    // Fonction pour vérifier les permissions
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
                OnboardingScreen(onContinueClicked = { navController.navigate(Screen.Cgu.route) })
            }

            composable(Screen.Cgu.route) {
                CGUScreen(
                    onContinueClicked = {
                        if (hasRequiredPermissions(context)) {
                            navController.navigate(Screen.Scanning.route) {
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
                    onContinueClicked = { ipRange, networkName ->
                        navController.navigate(Screen.NetworkScanning.createRoute(ipRange, networkName))
                    },
                    onHistoryClicked = {
                        navController.navigate(Screen.History.route)
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    history = scanHistory,
                    onItemClicked = { historyItem ->
                        // Charger les données de l'historique dans l'état global
                        scanResults = historyItem.devices
                        securityReport = historyItem.report
                        navController.navigate(Screen.ScanResults.route)
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.NetworkScanning.route,
                arguments = listOf(
                    navArgument("ipRange") { type = NavType.StringType },
                    navArgument("networkName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val ipRange = backStackEntry.arguments?.getString("ipRange") ?: ""
                val networkName = backStackEntry.arguments?.getString("networkName") ?: "Inconnu"
                
                NetworkScanningScreen(
                    ipRange = ipRange,
                    networkName = networkName,
                    onScanFinished = { devices, report, name ->
                        // 1. Sauvegarder dans l'historique
                        val newItem = ScanHistoryItem(
                            id = UUID.randomUUID().toString(),
                            date = Date(),
                            networkName = name,
                            devices = devices,
                            report = report
                        )
                        scanHistory.add(newItem)

                        // 2. Mettre à jour l'état actuel et naviguer
                        scanResults = devices
                        securityReport = report
                        navController.navigate(Screen.ScanResults.route)
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
