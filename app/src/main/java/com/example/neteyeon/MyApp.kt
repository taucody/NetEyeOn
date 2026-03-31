package com.example.neteyeon

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.neteyeon.screens.AllowingScreen
import com.example.neteyeon.screens.CGUScreen
import com.example.neteyeon.screens.OnboardingScreen
import com.example.neteyeon.screens.WifiScanScreen
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.neteyeon.Screen
import com.example.neteyeon.screens.NetworkScanningScreen

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

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
                    onScanClicked = { ipRange ->
                        navController.navigate(Screen.NetworkScanning.createRoute(ipRange))
                    }
                )
            }

            composable(Screen.NetworkScanning.route) { backStackEntry ->
                val ipRange = backStackEntry.arguments?.getString("ipRange") ?: ""

                NetworkScanningScreen(
                    ipRange = ipRange,
                    onScanClicked = { scanType ->
                        // suite
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
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
