package com.example.neteyeon.export

import android.content.Context
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.models.ScanHistoryItem
import com.example.neteyeon.models.Severity
import com.example.neteyeon.network.NetworkSecurityReport
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ReportExporter {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    private val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.FRANCE)

    // ── JSON ──────────────────────────────────────────────────────────────────
    fun exportJson(
        context: Context,
        item: ScanHistoryItem
    ): File {
        val root = JSONObject().apply {
            put("networkName", item.networkName)
            put("date", dateFormat.format(item.date))
            put("score", item.report.score)
            put("grade", item.report.grade)
            put("summary", item.report.summary)
            put("deviceCount", item.report.deviceCount)

            put("devices", JSONArray().also { arr ->
                item.devices.forEach { device ->
                    arr.put(JSONObject().apply {
                        put("ip", device.ip)
                        put("hostname", device.hostname ?: "")
                        put("mac", device.macAddress ?: "")
                        put("vendor", device.vendor ?: "")
                        put("openPorts", JSONArray(device.openPorts))
                        put("osGuess", device.osGuess ?: "")
                        put("latencyMs", device.latencyMs ?: -1)
                        put("flags", JSONArray(device.securityFlags.map { it.label }))
                    })
                }
            })

            put("securityIssues", JSONArray().also { arr ->
                item.report.allFlags.forEach { (device, flag) ->
                    arr.put(JSONObject().apply {
                        put("ip", device.ip)
                        put("flag", flag.label)
                        put("severity", flag.severity.name)
                    })
                }
            })
        }

        val fileName = "neteyeon_${fileNameFormat.format(item.date)}.json"
        val file = File(context.cacheDir, fileName)
        file.writeText(root.toString(2))
        return file
    }

    // ── CSV ───────────────────────────────────────────────────────────────────
    fun exportCsv(
        context: Context,
        item: ScanHistoryItem
    ): File {
        val sb = StringBuilder()
        sb.appendLine("# NetEyeON — Rapport de scan")
        sb.appendLine("# Réseau,${item.networkName}")
        sb.appendLine("# Date,${dateFormat.format(item.date)}")
        sb.appendLine("# Score,${item.report.score}/100 (${item.report.grade})")
        sb.appendLine()
        sb.appendLine("IP,Hostname,MAC,Fabricant,Ports ouverts,OS estimé,Latence (ms),Flags sécurité")

        item.devices.forEach { d ->
            sb.appendLine(
                listOf(
                    d.ip,
                    d.hostname ?: "",
                    d.macAddress ?: "",
                    d.vendor ?: "",
                    d.openPorts.joinToString(";"),
                    d.osGuess ?: "",
                    d.latencyMs?.toString() ?: "",
                    d.securityFlags.joinToString(";") { it.label }
                ).joinToString(",") { "\"$it\"" }
            )
        }

        sb.appendLine()
        sb.appendLine("Problèmes de sécurité")
        sb.appendLine("IP,Flag,Sévérité,Recommandation")
        item.report.allFlags.forEach { (device, flag) ->
            sb.appendLine("\"${device.ip}\",\"${flag.label}\",\"${flag.severity.name}\",\"${recommendation(flag.severity)}\"")
        }

        val fileName = "neteyeon_${fileNameFormat.format(item.date)}.csv"
        val file = File(context.cacheDir, fileName)
        file.writeText(sb.toString())
        return file
    }

    // ── PDF (iText / HTML→PDF via PrintManager workaround) ───────────────────
    // On génère un HTML puis on l'écrit dans un fichier .html partageable,
    // car iText nécessite une dépendance. Le HTML est complet et imprimable.
    fun exportHtmlAsPdf(
        context: Context,
        item: ScanHistoryItem
    ): File {
        val html = buildHtmlReport(item)
        val fileName = "neteyeon_${fileNameFormat.format(item.date)}.html"
        val file = File(context.cacheDir, fileName)
        file.writeText(html)
        return file
    }

    // ── Partage via Intent ────────────────────────────────────────────────────
    fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = ShareCompat.IntentBuilder(context)
            .setType(mimeType)
            .setStream(uri)
            .setChooserTitle("Partager le rapport")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    // ── HTML ──────────────────────────────────────────────────────────────────
    private fun buildHtmlReport(item: ScanHistoryItem): String {
        val gradeColor = when (item.report.grade) {
            "A" -> "#2E7D32"; "B" -> "#558B2F"; "C" -> "#F9A825"; "D" -> "#E65100"; else -> "#B71C1C"
        }

        val devicesRows = item.devices.joinToString("") { d ->
            val flagsBadges = d.securityFlags.joinToString(" ") { f ->
                val c = when (f.severity) {
                    Severity.CRITICAL -> "#B71C1C"; Severity.HIGH -> "#E65100"
                    Severity.MEDIUM -> "#F9A825"; Severity.LOW -> "#1565C0"
                }
                "<span style='background:$c;color:#fff;padding:2px 6px;border-radius:4px;font-size:11px;margin:2px'>${f.label}</span>"
            }
            """<tr>
                <td>${d.ip}</td>
                <td>${d.hostname ?: "—"}</td>
                <td>${d.macAddress ?: "—"}</td>
                <td>${d.vendor ?: "Inconnu"}</td>
                <td>${d.openPorts.joinToString(", ").ifEmpty { "—" }}</td>
                <td>${d.osGuess ?: "—"}</td>
                <td>${flagsBadges.ifEmpty { "✅ Aucun" }}</td>
            </tr>"""
        }

        val recommendations = buildRecommendations(item.report)

        return """<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Rapport NetEyeON — ${item.networkName}</title>
<style>
  body { font-family: Arial, sans-serif; margin: 40px; color: #212121; }
  h1 { color: #1A237E; } h2 { color: #283593; border-bottom: 2px solid #E8EAF6; padding-bottom: 8px; }
  .score-box { display:inline-block; background:$gradeColor; color:#fff; 
    width:80px; height:80px; border-radius:16px; text-align:center; 
    line-height:80px; font-size:42px; font-weight:bold; float:right; }
  .meta { color:#555; margin-bottom:24px; }
  .progress { background:#eee; border-radius:8px; height:14px; margin:8px 0; }
  .progress-bar { background:$gradeColor; height:14px; border-radius:8px; width:${item.report.score}%; }
  table { width:100%; border-collapse:collapse; margin-top:16px; font-size:13px; }
  th { background:#E8EAF6; padding:8px; text-align:left; }
  td { padding:8px; border-bottom:1px solid #eee; vertical-align:top; }
  .rec { background:#FFF8E1; border-left:4px solid #F9A825; padding:12px 16px; margin:8px 0; border-radius:4px; }
  .rec.critical { background:#FFEBEE; border-color:#B71C1C; }
  .rec.high { background:#FBE9E7; border-color:#E65100; }
  .rec.low { background:#E3F2FD; border-color:#1565C0; }
  footer { margin-top:40px; color:#999; font-size:12px; text-align:center; }
</style>
</head>
<body>
<h1>🛡️ Rapport de sécurité réseau</h1>
<div class="score-box">${item.report.grade}</div>
<div class="meta">
  <strong>Réseau :</strong> ${item.networkName}<br>
  <strong>Date :</strong> ${dateFormat.format(item.date)}<br>
  <strong>Appareils scannés :</strong> ${item.devices.size}
</div>
<div class="progress"><div class="progress-bar"></div></div>
<p><strong>Score de sécurité : ${item.report.score}/100</strong> — ${item.report.summary}</p>

<h2>Appareils détectés</h2>
<table>
  <tr><th>IP</th><th>Hostname</th><th>MAC</th><th>Fabricant</th><th>Ports ouverts</th><th>OS estimé</th><th>Flags</th></tr>
  $devicesRows
</table>

<h2>Recommandations de sécurité</h2>
$recommendations

<footer>Généré par NetEyeON · ${dateFormat.format(Date())}</footer>
</body></html>"""
    }

    private fun buildRecommendations(report: NetworkSecurityReport): String {
        if (report.allFlags.isEmpty()) return "<p>✅ Aucun problème détecté. Continuez à surveiller votre réseau régulièrement.</p>"

        val seen = mutableSetOf<String>()
        return report.allFlags
            .filter { (_, flag) -> seen.add(flag.name) }
            .joinToString("") { (_, flag) ->
                val cssClass = flag.severity.name.lowercase().let {
                    if (it == "medium") "medium" else it
                }
                val advice = flagAdvice(flag)
                "<div class='rec $cssClass'><strong>[${flag.severity.name}] ${flag.label}</strong><br>$advice</div>"
            }
    }

    private fun flagAdvice(flag: com.example.neteyeon.models.SecurityFlag): String = when (flag) {
        com.example.neteyeon.models.SecurityFlag.TELNET_OPEN ->
            "Désactivez Telnet immédiatement. Utilisez SSH à la place. Telnet transmet tout en clair, y compris les mots de passe."
        com.example.neteyeon.models.SecurityFlag.FTP_OPEN ->
            "Remplacez FTP par SFTP ou FTPS. FTP ne chiffre pas les données ni les identifiants."
        com.example.neteyeon.models.SecurityFlag.RDP_OPEN ->
            "N'exposez pas RDP directement. Utilisez un VPN ou limitez l'accès par IP. Activez l'authentification à deux facteurs."
        com.example.neteyeon.models.SecurityFlag.SSH_OPEN ->
            "Vérifiez que SSH utilise uniquement l'authentification par clé (désactivez les mots de passe). Changez le port par défaut (22)."
        com.example.neteyeon.models.SecurityFlag.HTTP_NO_HTTPS ->
            "Activez HTTPS sur ce service. HTTP transmet les données en clair. Utilisez un certificat TLS/SSL."
        com.example.neteyeon.models.SecurityFlag.MANY_PORTS_OPEN ->
            "Fermez les ports inutilisés. Chaque port ouvert est une surface d'attaque potentielle."
        com.example.neteyeon.models.SecurityFlag.UNKNOWN_DEVICE ->
            "Identifiez cet appareil. Un appareil inconnu sur votre réseau peut être un signe d'intrusion."
        com.example.neteyeon.models.SecurityFlag.OLD_SERVICE_BANNER ->
            "Mettez à jour le service détecté. Les versions anciennes contiennent souvent des vulnérabilités connues (CVE)."
    }

    private fun recommendation(severity: Severity) = when (severity) {
        Severity.CRITICAL -> "Action immédiate requise"
        Severity.HIGH -> "Corriger rapidement"
        Severity.MEDIUM -> "À planifier"
        Severity.LOW -> "Amélioration recommandée"
    }
}