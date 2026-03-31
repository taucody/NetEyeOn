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
import com.example.neteyeon.ui.theme.NetEyeOnTheme

@Composable
fun NetworkScanningScreen(
    ipRange: String,
    onScanClicked: (scanType: String) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scanType by remember { mutableStateOf("") }

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
                    .padding( 25.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { scanType = "Basique" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (scanType == "Basique")
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (scanType == "Basique")
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Basique")
                    }

                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { scanType = "Avance" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (scanType == "Avance")
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (scanType == "Avance")
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Avance")
                    }

                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { scanType = "Custom" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (scanType == "Custom")
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (scanType == "Custom")
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Custom")
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

            Button(onClick = { onScanClicked(scanType) }) {
                Text("Scanner", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview
@Composable
fun PreviewNetworkScanningScreen() {
    NetEyeOnTheme {
        NetworkScanningScreen(ipRange = "123.432.132/24", onScanClicked = {}, onBackClicked = {})
    }
}