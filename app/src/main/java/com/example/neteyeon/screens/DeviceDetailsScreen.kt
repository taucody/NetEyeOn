package com.example.neteyeon.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailsScreen(
    device: DiscoveredDevice,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails de l'appareil") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            DetailItem(label = "Adresse IP", value = device.ip)
            DetailItem(label = "Hostname", value = device.hostname ?: "Inconnu")
            DetailItem(label = "Adresse MAC", value = device.macAddress ?: "Inconnue")
            DetailItem(label = "Fabricant", value = device.vendor ?: "Inconnu")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Ports ouverts", style = MaterialTheme.typography.titleMedium)
            if (device.openPorts.isNotEmpty()) {
                Text(text = device.openPorts.joinToString(", "))
            } else {
                Text(text = "Aucun port ouvert détecté")
            }
            
            DetailItem(label = "Statut", value = if (device.isReachable) "En ligne" else "Hors ligne")
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceDetailsScreenPreview() {
    NetEyeOnTheme {
        DeviceDetailsScreen(
            device = DiscoveredDevice(
                ip = "192.168.1.15",
                hostname = "Living-Room-TV",
                macAddress = "00:11:22:33:44:55",
                vendor = "Samsung Electronics",
                openPorts = listOf(80, 443, 8080),
                isReachable = true
            ),
            onBackClicked = {}
        )
    }
}
