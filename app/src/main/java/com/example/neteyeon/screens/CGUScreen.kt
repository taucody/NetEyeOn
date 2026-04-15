package com.example.neteyeon.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neteyeon.ui.theme.NetEyeOnTheme

val cguText = """
    Conditions Générales d’Utilisation

    Les présentes Conditions Générales d’Utilisation encadrent l’usage de l’application NetEyeOn, une application Android de détection et d’analyse de sécurité d’un réseau local Wi‑Fi. L’application permet notamment d’identifier des appareils connectés, d’effectuer des vérifications réseau sur le sous-réseau local, d’afficher des résultats d’analyse et de générer des rapports exportables.

    En utilisant l’application, vous reconnaissez avoir lu, compris et accepté sans réserve les présentes Conditions Générales d’Utilisation. Si vous n’acceptez pas ces conditions, vous ne devez pas utiliser l’application.

    1. Objet de l’application

    NetEyeOn a pour finalité de fournir à l’utilisateur des informations techniques sur le réseau Wi‑Fi auquel son appareil Android est connecté, notamment afin d’illustrer ou d’évaluer certains aspects de sécurité du réseau local. L’application peut notamment récupérer des informations liées au réseau courant, identifier des hôtes présents sur le sous-réseau, détecter certains ports ouverts, afficher des indicateurs de risque et produire des rapports d’analyse.

    2. Conditions d’usage autorisé

    Vous vous engagez à utiliser l’application uniquement sur :
    - votre propre réseau ;
    - un réseau dont vous êtes administrateur ;
    - ou un réseau pour lequel vous avez obtenu une autorisation explicite préalable.

    Toute utilisation sur un réseau tiers sans autorisation peut être illégale, contraire aux règles de sécurité applicables ou contraire aux conditions imposées par un fournisseur d’accès, un établissement ou une organisation. Vous êtes seul responsable de la vérification de vos droits avant tout lancement d’un scan ou d’une analyse.

    3. Permissions demandées

    L’application peut demander certaines permissions Android nécessaires à son fonctionnement, notamment l’accès à Internet, l’accès à l’état du réseau, l’accès à l’état du Wi‑Fi, ainsi que des permissions de localisation. Sous Android, certaines informations Wi‑Fi, telles que le SSID ou le BSSID, peuvent nécessiter une autorisation de localisation pour être accessibles à l’application.

    Le refus de certaines permissions peut empêcher le fonctionnement normal de certaines fonctionnalités, notamment l’identification du réseau Wi‑Fi actif et certaines opérations d’analyse.

    4. Données traitées

    Dans le cadre de son fonctionnement, l’application peut traiter des données techniques liées au réseau local et aux équipements détectés, par exemple :
    - le nom du réseau Wi‑Fi (SSID) ;
    - l’identifiant du point d’accès (BSSID) ;
    - les adresses IP détectées ;
    - les adresses MAC visibles ;
    - certains ports ouverts ;
    - des informations techniques issues du scan ;
    - les fichiers de rapport générés par l’utilisateur.

    Ces données sont utilisées exclusivement pour fournir la fonctionnalité d’analyse réseau et la génération de rapports au sein de l’application.

    5. Export et partage de rapports

    L’application peut permettre la génération de rapports aux formats PDF, JSON ou CSV, puis leur partage via les mécanismes Android compatibles. Le partage de ces fichiers s’effectue via un mécanisme sécurisé de type FileProvider, recommandé sur Android pour accorder un accès limité aux fichiers partagés avec d’autres applications.

    Vous êtes seul responsable du contenu des rapports exportés ainsi que de leur transmission à des tiers. Avant tout partage, vous devez vous assurer que vous êtes autorisé à communiquer les informations contenues dans ces rapports.

    6. Responsabilité de l’utilisateur

    Vous êtes seul responsable :
    - de l’usage que vous faites de l’application ;
    - du choix des réseaux analysés ;
    - des conséquences d’un scan lancé sans autorisation ;
    - de l’interprétation des résultats affichés ;
    - du partage ou de la conservation des rapports exportés.

    Vous vous engagez à ne pas utiliser l’application à des fins de nuisance, d’intrusion, de perturbation, de contournement de sécurité ou d’atteinte à la confidentialité de tiers.

    7. Limites de garantie

    L’application est fournie “en l’état”, sans garantie d’exactitude, d’exhaustivité ou d’adéquation à un besoin particulier. Les résultats affichés ont une valeur indicative et dépendent notamment de l’environnement réseau, des restrictions Android, des permissions accordées, de la connectivité et du comportement des équipements scannés.

    En conséquence, l’application ne garantit ni la détection de toutes les vulnérabilités ni l’absence totale d’erreurs, de faux positifs ou de faux négatifs.

    8. Disponibilité et évolution

    Les fonctionnalités de l’application peuvent être modifiées, limitées, suspendues ou supprimées à tout moment, notamment dans le cadre d’un développement, d’un projet académique, d’une démonstration ou d’une amélioration technique.

    9. Propriété intellectuelle

    Sauf mention contraire, l’application, son interface, son code, ses éléments graphiques, ses textes et sa structure générale sont protégés par les règles applicables en matière de propriété intellectuelle. Toute reproduction, diffusion ou réutilisation non autorisée est interdite.

    10. Acceptation

    En cochant la case “J’ai lu et j’accepte les CGU” puis en poursuivant l’utilisation de l’application, vous reconnaissez accepter les présentes Conditions Générales d’Utilisation.
""".trimIndent()

@Composable
fun CGUScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var acceptCgu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { acceptCgu = !acceptCgu },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = acceptCgu,
                            onCheckedChange = { acceptCgu = it }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "J’ai lu et j’accepte les CGU",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = onContinueClicked,
                        enabled = acceptCgu,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continuer")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Conditions Générales d’Utilisation",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Veuillez lire attentivement les conditions avant de continuer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    Text(
                        text = cguText,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CGUScreenPreview() {
    NetEyeOnTheme {
        CGUScreen(onContinueClicked = {})
    }
}