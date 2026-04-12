package com.example.neteyeon.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import androidx.compose.material3.Icon
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Locate
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wifi
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun AllowingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier) {

    val context = LocalContext.current

    var isLocationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isLocationGranted = isGranted  // ✅ Met à jour l'état
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Lucide.Key,
                contentDescription = "Key"
            )

            Text(
                text = "NETeyeON a besoin de votre autorisation",
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(100.dp))
            val prefs = context.getSharedPreferences("permissions", Context.MODE_PRIVATE)

            Row(
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Surface(
                    color = if (isLocationGranted) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                        .clickable {
                            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Locate,
                            contentDescription = "Location"
                        )

                        Column {
                            Text(
                                text = "Accès à la localisation"
                            )
                            Text(
                                text = "Requise par Android pour accéder aux informations Wi-Fi",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(150.dp))


            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                enabled = isLocationGranted,
                onClick = onContinueClicked
            ) {
                Text("Continuer")
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun AllowingPreview() {
    NetEyeOnTheme {
        AllowingScreen( onContinueClicked = {})
    }
}
