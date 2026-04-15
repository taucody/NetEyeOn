package com.example.neteyeon.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wifi
import java.net.Inet4Address


@Composable
fun WifiScanScreen(
    onContinueClicked: (ipRange: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("WIFI_SCAN", "Composable démarré")
    val context = LocalContext.current
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // États
    var ssid by remember { mutableStateOf<String?>(null) }
    var ipAddress by remember { mutableStateOf<String?>(null) }
    var bssid by remember { mutableStateOf<String?>(null) }
    var rssi by remember { mutableStateOf<Int?>(null) }
    var ipRange by remember { mutableStateOf("Indisponible") }
    var availableNetworks by remember { mutableStateOf<List<ScanResult>>(emptyList()) }

    // Fonction de mise à jour
    fun updateNetworkInfo() {
        val network = connectivityManager.activeNetwork
        val linkProperties = connectivityManager.getLinkProperties(network)
        
        // Sur Android 14+, WifiManager.connectionInfo nécessite ACCESS_FINE_LOCATION et le GPS activé
        @Suppress("DEPRECATION")
        val wifiInfo = wifiManager.connectionInfo

        if (wifiInfo != null) {
            val rawSsid = wifiInfo.ssid.removePrefix("\"").removeSuffix("\"")
            ssid = if (rawSsid == "<unknown ssid>" || rawSsid == "0.0.0.0") null else rawSsid
            
            val rawBssid = wifiInfo.bssid
            bssid = if (rawBssid == "02:00:00:00:00:00" || rawBssid == null) null else rawBssid
            
            rssi = if (wifiInfo.rssi == -127) null else wifiInfo.rssi
        }

        val linkAddress = linkProperties?.linkAddresses
            ?.firstOrNull { it.address is Inet4Address }

        ipAddress = linkAddress?.address?.hostAddress

        if (linkAddress != null) {
            val address = linkAddress.address
            val prefixLength = linkAddress.prefixLength
            val bytes = address.address

            // Calcul de l'adresse réseau
            var ipInt = 0
            for (b in bytes) {
                ipInt = (ipInt shl 8) or (b.toInt() and 0xFF)
            }

            val mask = if (prefixLength == 0) 0 else (-1 shl (32 - prefixLength))
            val networkInt = ipInt and mask

            val n1 = (networkInt shr 24) and 0xFF
            val n2 = (networkInt shr 16) and 0xFF
            val n3 = (networkInt shr 8) and 0xFF
            val n4 = networkInt and 0xFF

            ipRange = "$n1.$n2.$n3.$n4/$prefixLength"
        } else {
            ipRange = "Indisponible"
        }
    }

    // BroadcastReceiver
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    WifiManager.NETWORK_STATE_CHANGED_ACTION,
                    ConnectivityManager.CONNECTIVITY_ACTION -> {
                        updateNetworkInfo()
                    }
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        try {
                            val results = wifiManager.scanResults
                            Log.d("WIFI_SCAN", "Nombre de réseaux trouvés: ${results.size}")
                            availableNetworks = results
                                .distinctBy {
                                    if (it.SSID.isEmpty()) it.BSSID
                                    else it.SSID
                                }
                        } catch (e: SecurityException) {
                            Log.e("WIFI_SCAN", "SecurityException: ${e.message}")
                            availableNetworks = emptyList()
                        }
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }

        context.registerReceiver(receiver, filter)
        updateNetworkInfo()

        try {
            @Suppress("DEPRECATION")
            wifiManager.startScan()
        } catch (e: SecurityException) {
            Log.e("WIFI_SCAN", "SecurityException startScan: ${e.message}")
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Scanner un réseau Wi-Fi", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),

                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Lucide.Wifi,
                        contentDescription = "Wifi"
                    )

                    Column {
                        Text(text = "Réseau Wi-Fi actuel")

                        if (ipAddress == null) {
                            Text(
                                text = "Non connecté au Wi-Fi",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        } else {
                            Text(text = "Nom : ${ssid ?: "Inconnu (Activez le GPS)"}")
                            Text(
                                text = "IP : $ipAddress",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "MAC : ${bssid ?: "Masquée (Permissions requises)"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Signal : ${rssi ?: "N/A"} dBm",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Réseaux à proximité", style = MaterialTheme.typography.titleMedium)

        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            items(availableNetworks) { network ->
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Lucide.Wifi, contentDescription = "Wifi")
                        Column {
                            Text(text = network.SSID.ifEmpty { "Réseau caché" })
                            Text(
                                text = "Signal : ${network.level} dBm · ${if (network.frequency > 4000) "5GHz" else "2.4GHz"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onContinueClicked(ipRange) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuer")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WifiScanScreenPreview() {
    WifiScanScreen(onContinueClicked = {})
}
