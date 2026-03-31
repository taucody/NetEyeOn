package com.example.neteyeon.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.neteyeon.models.DiscoveredDevice

@Composable
fun DeviceItem(
    device: DiscoveredDevice,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = device.ip, style = MaterialTheme.typography.bodyLarge)
            device.hostname?.let {
                Text(text = "Hostname: $it", style = MaterialTheme.typography.bodyMedium)
            }
            if (device.openPorts.isNotEmpty()) {
                Text(
                    text = "Ports ouverts: ${device.openPorts.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
