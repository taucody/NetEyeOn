package com.example.neteyeon.screens

import android.Manifest
import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Locate
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Wifi
import com.example.neteyeon.models.DiscoveredDevice
import com.example.neteyeon.network.NetworkScanner
import kotlinx.coroutines.launch
import java.net.Inet4Address


@Composable
fun WifiScanScreen(
    onContinueClicked: (ipRange: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // États
    var ssid by remember { mutableStateOf<String?>(null) }
    var ipAddress by remember { mutableStateOf<String?>(null) }
    var bssid by remember { mutableStateOf<String?>(null) }
    var rssi by remember { mutableStateOf<Int?>(null) }
    var ipRange by remember { mutableStateOf("Indisponible") }

    // Fonction de mise à jour
    fun updateNetworkInfo() {
        val networkInfo = connectivityManager.activeNetwork
        val linkProperties = connectivityManager.getLinkProperties(networkInfo)
        val capabilities = connectivityManager.getNetworkCapabilities(networkInfo)
        val wifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            capabilities?.transportInfo as? WifiInfo
        } else {
            @Suppress("DEPRECATION")
            wifiManager.connectionInfo
        }

        ssid = wifiInfo?.ssid
        bssid = wifiInfo?.bssid
        rssi = wifiInfo?.rssi
        ipAddress = linkProperties?.linkAddresses
            ?.firstOrNull { it.address is Inet4Address }
            ?.address
            ?.hostAddress
        ipRange = ipAddress ?: "Indisponible"
    }

    // ✅ BroadcastReceiver
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                updateNetworkInfo()
            }
        }

        val filter = IntentFilter().apply {
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        }

        context.registerReceiver(receiver, filter)
        updateNetworkInfo() // Charge au démarrage

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                        Text(text = "Réseau Wi-Fi actuellement connecté")

                        if (ssid == null || ipAddress == null) {
                            Text(
                                text = "Veuillez vous connecter à un réseau Wi-Fi",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        } else {
                            Text(text = "Nom du réseau : $ssid")
                            Text(
                                text = "Adresse IP : $ipAddress",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Adresse MAC : $bssid",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Force du signal : $rssi",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ipRange,
            onValueChange = { ipRange = it },
            label = { Text("Plage d'adresses IP (ex: 192.168.1.0/24)") },
            modifier = Modifier.fillMaxWidth()
        )
        
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
