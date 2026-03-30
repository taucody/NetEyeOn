package com.example.neteyeon

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.neteyeon.screens.AllowingScreen
import com.example.neteyeon.screens.CGUScreen
import com.example.neteyeon.screens.OnboardingScreen
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var shouldShowOnboarding by remember { mutableStateOf(true) }
    var shouldShowAllowing by remember { mutableStateOf(false) }

    Surface(modifier = modifier) {
        when {
            shouldShowOnboarding -> {
                OnboardingScreen(
                    onContinueClicked = { shouldShowOnboarding = false }
                )
            }

            !shouldShowAllowing -> {
                CGUScreen(
                    onContinueClicked = { shouldShowAllowing = true }
                )
            }

            else -> {
                AllowingScreen()
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