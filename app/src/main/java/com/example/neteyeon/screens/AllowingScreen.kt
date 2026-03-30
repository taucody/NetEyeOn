package com.example.neteyeon.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun AllowingScreen(modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary) {
        Text(
            text = "NETeyeON a besoin de votre autorisation",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AllowingPreview() {
    NetEyeOnTheme {
        AllowingScreen()
    }
}