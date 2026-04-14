package com.example.neteyeon.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Locate
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Radar
import com.example.neteyeon.screens.CGUScreen
import com.example.neteyeon.components.AcceptCguCheckbox
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.network.NetworkScanner
import com.example.neteyeon.network.NetworkSecurityReport
import com.example.neteyeon.network.SecurityScorer
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import kotlinx.coroutines.launch

@Composable
fun NetworkScanningScreen(
    ipRange: String,
    onScanFinished: (List<DiscoveredDevice>, NetworkSecurityReport) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scanType by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val scanner = remember { NetworkScanner() }

    val scanDescription = when (scanType) {
        "Basique" -> "Scan simple et rapide des hôtes disponibles."
        "Avance" -> "Scan optimisé pour obtenir des résultats en peu de temps."
        "Custom" -> "Scan plus complet avec davantage de vérifications réseau."
        else -> "Sélectionnez un profil de scan."
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Retour"
                    )
                }
            }

            Icon(
                imageVector = Lucide.Radar,
                contentDescription = "Radar",
                modifier = Modifier
                    .size(150.dp)
                    .padding(25.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text("Prêt à Scanner le réseau suivant: ", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .heightIn(min = 80.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text("Plage à scanner", style = MaterialTheme.typography.bodySmall)

                    Text(text = ipRange, style = MaterialTheme.typography.bodyLarge)

                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Profil de scan",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(0.85f),
                textAlign = TextAlign.Start
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.85f),
                thickness = 3.dp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("Basique", "Avance", "Custom").forEach { type ->
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = { scanType = type },
                            enabled = !isScanning,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (scanType == type)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (scanType == type)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(type)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.85f)

            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text("Description du profil de scan")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = scanDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


            }

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    if (scanType.isNotEmpty() && !isScanning) {
                        scope.launch {
                            isScanning = true
                            progress = 0f
                            val results = scanner.scanRange(ipRange) { current, total ->
                                progress = current.toFloat() / total.toFloat()
                            }
                            val report = SecurityScorer.evaluate(results)
                            isScanning = false
                            onScanFinished(results, report)
                        }
                    }
                },
                enabled = scanType.isNotEmpty() && !isScanning
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan en cours...")
                } else {
                    Text("Scanner", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (isScanning) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewNetworkScanningScreen() {
    NetEyeOnTheme {
        NetworkScanningScreen(
            ipRange = "192.168.1.0/24",
            onScanFinished = {} as (List<DiscoveredDevice>, NetworkSecurityReport) -> Unit,
            onBackClicked = {}
        )
    }
}