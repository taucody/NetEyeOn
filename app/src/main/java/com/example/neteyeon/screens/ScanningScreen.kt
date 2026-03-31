package com.example.neteyeon.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.network.NetworkScanner
import kotlinx.coroutines.launch

@Composable
fun WifiScanScreen(
    onScanFinished: (List<DiscoveredDevice>) -> Unit,
    modifier: Modifier = Modifier
) {
    var ipRange by remember { mutableStateOf("192.168.1.0/24") }
    var scanning by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    
    val scope = rememberCoroutineScope()
    val scanner = remember { NetworkScanner() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Scanner un réseau Wi-Fi", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = ipRange,
            onValueChange = { ipRange = it },
            label = { Text("Plage d'adresses IP (ex: 192.168.1.0/24)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !scanning
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                scanning = true
                progress = 0f
                scope.launch {
                    val results = scanner.scanRange(ipRange) { current, total ->
                        progress = current.toFloat() / total.toFloat()
                    }
                    scanning = false
                    onScanFinished(results)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !scanning
        ) {
            if (scanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan en cours...")
            } else {
                Text("Lancer le Scan")
            }
        }

        if (scanning) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WifiScanScreenPreview() {
    WifiScanScreen(onScanFinished = {})
}
