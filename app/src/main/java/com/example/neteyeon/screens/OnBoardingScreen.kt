package com.example.neteyeon.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.R
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mini_logo),
            contentDescription = "Logo NETeyeON",
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 16.dp)
        )
        Text("NETeyeON")
        Text("network scanner")

        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        )

        {
            Text("Continuer")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    NetEyeOnTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}