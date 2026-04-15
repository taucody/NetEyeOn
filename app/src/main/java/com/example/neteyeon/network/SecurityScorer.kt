package com.example.neteyeon.network

import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.models.SecurityFlag
import com.example.neteyeon.models.Severity

data class NetworkSecurityReport(
    val score: Int,                              // 0–100
    val grade: String,                           // A / B / C / D / F
    val summary: String,
    val allFlags: List<Pair<DiscoveredDevice, SecurityFlag>>,  // tous les problèmes
    val deviceCount: Int
)

object SecurityScorer {

    // Pénalité par sévérité
    private val penaltyPerFlag = mapOf(
        Severity.CRITICAL to 20,
        Severity.HIGH     to 12,
        Severity.MEDIUM   to 6,
        Severity.LOW      to 2
    )

    fun evaluate(devices: List<DiscoveredDevice>): NetworkSecurityReport {
        var score = 100
        val allFlags = mutableListOf<Pair<DiscoveredDevice, SecurityFlag>>()

        for (device in devices) {
            for (flag in device.securityFlags) {
                val penalty = penaltyPerFlag[flag.severity] ?: 0
                score -= penalty
                allFlags += device to flag
            }
        }

        // réseau avec peu d'appareils inconnus
        val unknownRatio = devices.count { it.vendor == null }.toDouble() / devices.size.coerceAtLeast(1)
        if (unknownRatio > 0.5) score -= 10      // >50% appareils non identifiés

        score = score.coerceIn(0, 100)

        val grade = when {
            score >= 90 -> "A"
            score >= 75 -> "B"
            score >= 60 -> "C"
            score >= 40 -> "D"
            else        -> "F"
        }

        val summary = when (grade) {
            "A" -> "Réseau bien sécurisé. Continuez à surveiller."
            "B" -> "Bonne sécurité, quelques points à améliorer."
            "C" -> "Sécurité moyenne. Des vulnérabilités sont présentes."
            "D" -> "Sécurité insuffisante. Agissez rapidement."
            else -> "Réseau très exposé. Intervention urgente recommandée."
        }

        return NetworkSecurityReport(
            score       = score,
            grade       = grade,
            summary     = summary,
            allFlags    = allFlags,
            deviceCount = devices.size
        )
    }
}