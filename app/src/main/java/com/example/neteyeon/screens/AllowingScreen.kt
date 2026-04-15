package com.example.neteyeon.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import com.composables.icons.lucide.Key
import com.composables.icons.lucide.Locate
import com.composables.icons.lucide.Lucide

@Composable
fun AllowingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier) {

    val context = LocalContext.current

    // Sur Android 14, on vérifie si la localisation PRÉCISE est accordée
    var isLocationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // On utilise RequestMultiplePermissions pour Android 12+
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
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
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))

            Surface(
                color = if (isLocationGranted) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .clickable {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
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
                            text = if (isLocationGranted) "Localisation autorisée" else "Accès à la localisation",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Requise par Android pour accéder aux informations Wi-Fi (SSID/BSSID)",
                            style = MaterialTheme.typography.bodySmall
                        )
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
            
            if (!isLocationGranted) {
                Text(
                    text = "Veuillez accorder la permission 'Précise' pour identifier le réseau.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "N'oubliez pas d'activer le GPS dans vos réglages rapides.",
                    style = MaterialTheme.typography.bodySmall
                )
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
