package com.example.neteyeon.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.Lucide
import com.example.neteyeon.components.DeviceItem
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.models.Severity
import com.example.neteyeon.network.NetworkSecurityReport
import com.example.neteyeon.network.SecurityScorer
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun ScanResultsScreen(
    devices: List<DiscoveredDevice>,
    report: NetworkSecurityReport?,
    onBackClicked: () -> Unit,
    onDeviceClicked: (DiscoveredDevice) -> Unit,
    onExportClicked: () -> Unit,
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

        // Score de sécurité
        report?.let { r ->
            SecurityScoreCard(report = r)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Nombre d'appareils
        Text(
            text = "Appareils trouvés : ${devices.size}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Liste des appareils
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(devices) { device ->
                DeviceItem(
                    device = device,
                    onClick = { onDeviceClicked(device) }
                )
            }

            // Détail des flags de sécurité en bas de la liste
            report?.let { r ->
                if (r.allFlags.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Problèmes détectés",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(r.allFlags) { (device, flag) ->
                        SecurityFlagItem(device = device, flag = flag)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nouveau Scan")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onExportClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Lucide.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Télécharger le rapport du scan")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Carte du score

@Composable
fun SecurityScoreCard(report: NetworkSecurityReport) {
    val gradeColor = when (report.grade) {
        "A"  -> Color(0xFF2E7D32)   // vert foncé
        "B"  -> Color(0xFF558B2F)   // vert clair
        "C"  -> Color(0xFFF9A825)   // jaune
        "D"  -> Color(0xFFE65100)   // orange
        else -> Color(0xFFB71C1C)   // rouge
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Score numérique + résumé
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Score de sécurité",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { report.score / 100f },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(8.dp),
                    color = gradeColor,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    report.summary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Grade
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(gradeColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    report.grade,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Item d'un flag de sécurité

@Composable
fun SecurityFlagItem(
    device: DiscoveredDevice,
    flag: com.example.neteyeon.models.SecurityFlag
) {
    val severityColor = when (flag.severity) {
        Severity.CRITICAL -> Color(0xFFB71C1C)
        Severity.HIGH     -> Color(0xFFE65100)
        Severity.MEDIUM   -> Color(0xFFF9A825)
        Severity.LOW      -> Color(0xFF1565C0)
    }

    Surface(
        color = severityColor.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge sévérité
            Surface(
                color = severityColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    flag.severity.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    flag.label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    device.ip + (device.hostname?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
    val sampleReport = SecurityScorer.evaluate(sampleDevices)

    NetEyeOnTheme {
        ScanResultsScreen(
            devices = sampleDevices,
            report = sampleReport,
            onBackClicked = {},
            onDeviceClicked = {},
            onExportClicked = {}
        )
    }
}