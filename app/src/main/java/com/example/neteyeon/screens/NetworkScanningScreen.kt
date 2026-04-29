package com.example.neteyeon.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Radar
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.network.NetworkScanner
import com.example.neteyeon.network.NetworkSecurityReport
import com.example.neteyeon.network.SecurityScorer
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import kotlinx.coroutines.launch

@Composable
fun NetworkScanningScreen(
    ipRange: String,
    networkName: String,
    onScanFinished: (List<DiscoveredDevice>, NetworkSecurityReport, String) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scanType by remember { mutableStateOf("Basique") }
    var customPortsString by remember { mutableStateOf("21, 22, 23, 80, 443, 3306, 3389, 8080") }
    var isScanning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val scanner = remember { NetworkScanner() }

    val scanDescription = when (scanType) {
        "Basique" -> "Scan simple et rapide des ports essentiels (80, 443, 22, 23)."
        "Avance" -> "Scan approfondi incluant les services communs (FTP, SSH, RDP, DB...)."
        "Custom" -> "Configurez manuellement les ports à scanner."
        else -> "Sélectionnez un profil de scan."
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                    .size(120.dp)
                    .padding(16.dp)
            )

            Text("Prêt à Scanner le réseau :", style = MaterialTheme.typography.titleLarge)
            Text(text = networkName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Plage d'adresses IP", style = MaterialTheme.typography.labelMedium)
                    Text(text = ipRange, style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Profil de scan",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.fillMaxWidth(0.85f),
                textAlign = TextAlign.Start
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.85f),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Basique", "Avance", "Custom").forEach { type ->
                        FilterChip(
                            selected = scanType == type,
                            onClick = { scanType = type },
                            label = { Text(type) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            enabled = !isScanning
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Information",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scanDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (scanType == "Custom") {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = customPortsString,
                            onValueChange = { customPortsString = it },
                            label = { Text("Ports (séparés par des virgules)") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isScanning,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("Ex: 80, 443, 8080") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (scanType.isNotEmpty() && !isScanning) {
                        scope.launch {
                            try {
                                isScanning = true
                                progress = 0f

                                val portsList = if (scanType == "Custom") {
                                    customPortsString.split(",")
                                        .mapNotNull { it.trim().toIntOrNull() }
                                } else null

                                val results = scanner.scanRange(
                                    ipRange = ipRange,
                                    scanProfile = scanType,
                                    customPorts = portsList
                                ) { current, total ->
                                    progress = if (total > 0) {
                                        current.toFloat() / total.toFloat()
                                    } else {
                                        0f
                                    }
                                }

                                val report = SecurityScorer.evaluate(results)
                                onScanFinished(results, report, networkName)
                            } catch (e: Exception) {
                                Log.e("SCAN_ERROR", "Erreur pendant le scan", e)
                            } finally {
                                isScanning = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = scanType.isNotEmpty() && !isScanning
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Analyse en cours...")
                } else {
                    Text("Lancer l'analyse", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (isScanning) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% complété",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview
@Composable
fun PreviewNetworkScanningScreen() {
    NetEyeOnTheme {
        NetworkScanningScreen(
            ipRange = "192.168.1.0/24",
            networkName = "Mon Wifi",
            onScanFinished = { _, _, _ -> },
            onBackClicked = {}
        )
    }
}
