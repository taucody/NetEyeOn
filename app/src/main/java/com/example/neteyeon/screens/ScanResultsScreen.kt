package com.example.neteyeon.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.components.DeviceItem
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun ScanResultsScreen(
    devices: List<DiscoveredDevice>,
    onBackClicked: () -> Unit,
    onDeviceClicked: (DiscoveredDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Résultats du Scan", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Appareils trouvés : ${devices.size}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(devices) { device ->
                DeviceItem(
                    device = device,
                    onClick = { onDeviceClicked(device) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nouveau Scan")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScanResultsScreenPreview() {
    val sampleDevices = listOf(
        DiscoveredDevice(ip = "192.168.1.1", hostname = "Router", isReachable = true),
        DiscoveredDevice(ip = "192.168.1.5", hostname = "My-Laptop", openPorts = listOf(80, 443), isReachable = true),
        DiscoveredDevice(ip = "192.168.1.10", vendor = "Apple Inc.", isReachable = true)
    )
    NetEyeOnTheme {
        ScanResultsScreen(
            devices = sampleDevices,
            onBackClicked = {},
            onDeviceClicked = {}
        )
    }
}
