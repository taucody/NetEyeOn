package com.example.neteyeon.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Share2
import com.composables.icons.lucide.Upload
import com.example.neteyeon.export.ReportExporter
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.models.ScanHistoryItem
import com.example.neteyeon.network.NetworkSecurityReport
import com.example.neteyeon.network.SecurityScorer
import com.example.neteyeon.ui.theme.NetEyeOnTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

enum class ExportFormat(val label: String, val mime: String) {
    PDF("PDF", "text/html"),       // HTML rendu comme PDF
    JSON("JSON", "application/json"),
    CSV("CSV", "text/csv")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportReportScreen(
    historyItem: ScanHistoryItem,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.FRANCE) }

    var selectedFormat by remember { mutableStateOf(ExportFormat.PDF) }
    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exporter le rapport") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // ── Icône upload ───────────────────────────────────────────────
            Icon(
                imageVector = Lucide.Upload,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rapport du scan de ${historyItem.networkName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "effectué le ${dateFormat.format(historyItem.date)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Contenu du rapport ─────────────────────────────────────────
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Contenu du rapport",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ReportContentLine("${historyItem.devices.size} appareils détectés")
                    ReportContentLine("${historyItem.report.allFlags.size} problèmes de sécurité")
                    ReportContentLine("Score de sécurité : ${historyItem.report.score}/100 (${historyItem.report.grade})")
                    ReportContentLine("Recommandations personnalisées par flag")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Format d'export ────────────────────────────────────────────
            Text(
                "Format d'export",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            ExportFormat.entries.forEach { format ->
                FormatOption(
                    format = format,
                    selected = selectedFormat == format,
                    onSelect = { selectedFormat = format }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bouton Exporter ────────────────────────────────────────────
            Button(
                onClick = {
                    scope.launch {
                        isExporting = true
                        try {
                            val file = withContext(Dispatchers.IO) {
                                when (selectedFormat) {
                                    ExportFormat.PDF  -> ReportExporter.exportHtmlAsPdf(context, historyItem)
                                    ExportFormat.JSON -> ReportExporter.exportJson(context, historyItem)
                                    ExportFormat.CSV  -> ReportExporter.exportCsv(context, historyItem)
                                }
                            }
                            // Ouvre le sélecteur de partage/sauvegarde
                            ReportExporter.shareFile(context, file, selectedFormat.mime)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isExporting = false
                        }
                    }
                },
                enabled = !isExporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isExporting) "Génération..." else "Exporter",
                    style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Bouton Partager ────────────────────────────────────────────
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isExporting = true
                        try {
                            val file = withContext(Dispatchers.IO) {
                                when (selectedFormat) {
                                    ExportFormat.PDF  -> ReportExporter.exportHtmlAsPdf(context, historyItem)
                                    ExportFormat.JSON -> ReportExporter.exportJson(context, historyItem)
                                    ExportFormat.CSV  -> ReportExporter.exportCsv(context, historyItem)
                                }
                            }
                            ReportExporter.shareFile(context, file, selectedFormat.mime)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isExporting = false
                        }
                    }
                },
                enabled = !isExporting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Lucide.Share2, contentDescription = null,
                    modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Partager", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReportContentLine(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun FormatOption(
    format: ExportFormat,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(selected = selected, onClick = null)
            Text(
                format.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExportReportScreenPreview() {
    val sampleItem = ScanHistoryItem(
        id = "1",
        date = Date(),
        networkName = "Freebox-XXXXX",
        devices = listOf(
            DiscoveredDevice(ip = "192.168.1.1", hostname = "Router", isReachable = true),
            DiscoveredDevice(ip = "192.168.1.5", openPorts = listOf(80, 22), isReachable = true)
        ),
        report = SecurityScorer.evaluate(listOf(
            DiscoveredDevice(ip = "192.168.1.1", isReachable = true),
            DiscoveredDevice(ip = "192.168.1.5", openPorts = listOf(80, 22), isReachable = true)
        ))
    )
    NetEyeOnTheme {
        ExportReportScreen(historyItem = sampleItem, onBackClicked = {})
    }
}