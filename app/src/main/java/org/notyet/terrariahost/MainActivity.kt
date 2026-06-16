package org.notyet.terrariahost

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.notyet.terrariahost.data.AndroidNetworkInterfaceProvider
import org.notyet.terrariahost.data.ServerEventBus
import org.notyet.terrariahost.domain.ServerState
import org.notyet.terrariahost.domain.network.NetworkAddress
import org.notyet.terrariahost.domain.network.NetworkAddressType
import org.notyet.terrariahost.domain.network.NetworkDetector
import org.notyet.terrariahost.service.TerrariaServerService
import org.notyet.terrariahost.ui.NotyetTheme

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        refreshNetworks()
        setContent {
            NotyetTheme {
                val state by ServerEventBus.state.collectAsState()
                HostScreen(
                    state = state,
                    onStart = { ContextCompat.startForegroundService(this, TerrariaServerService.startIntent(this)) },
                    onStop = { startService(TerrariaServerService.stopIntent(this)) },
                    onRefreshNetworks = { refreshNetworks() },
                )
            }
        }
    }

    private fun refreshNetworks() {
        val detector = NetworkDetector(AndroidNetworkInterfaceProvider())
        ServerEventBus.update { it.copy(networkAddresses = detector.detect()) }
    }
}

@Composable
fun HostScreen(state: ServerState, onStart: () -> Unit, onStop: () -> Unit, onRefreshNetworks: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Notyet Terraria Host", style = MaterialTheme.typography.headlineMedium)
            Text("v0.2 LAN + existing VPN address helper. This app does not include or distribute Terraria server files.")

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Status: ${if (state.isRunning) "Running" else "Stopped"}")
                    Text("Local address: ${state.connectionAddress}")
                    Text("Use this address from another device on the same Wi‑Fi/LAN.")
                    Text("Expected user-provided server launcher: ${state.expectedExecutablePath}")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.widthIn(max = 520.dp)) {
                        Button(enabled = !state.isRunning, onClick = onStart) { Text("Start") }
                        Button(enabled = state.isRunning, onClick = onStop) { Text("Stop") }
                        Button(onClick = { clipboard.setText(AnnotatedString(state.connectionAddress)) }) { Text("Copy") }
                    }
                }
            }

            NetworkAddressesCard(
                addresses = state.networkAddresses,
                port = state.port,
                onRefresh = onRefreshNetworks,
                onCopy = { clipboard.setText(AnnotatedString(it)) },
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("v0.2 limits", style = MaterialTheme.typography.titleMedium)
                    Text("LAN works on the same Wi‑Fi. VPN addresses require friends to already be in the same VPN/overlay network.")
                    Text("No paid app servers and no bundled Terraria files. Provide your own legal server launcher in the app files directory.")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Logs", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(state.logs.ifEmpty { listOf("No logs yet.") }.joinToString("\n"))
                }
            }
        }
    }
}


@Composable
private fun NetworkAddressesCard(
    addresses: List<NetworkAddress>,
    port: Int,
    onRefresh: () -> Unit,
    onCopy: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("v0.2 detected connection addresses", style = MaterialTheme.typography.titleMedium)
            Text("Copy the LAN address for same Wi‑Fi. VPN addresses only work when friends are already in the same VPN network.")
            Button(onClick = onRefresh) { Text("Refresh networks") }
            if (addresses.isEmpty()) {
                Text("No usable LAN/VPN IPv4 address detected yet.")
            } else {
                addresses.forEach { address ->
                    val connectionAddress = address.connectionAddress(port)
                    Text("${address.type.label()}: $connectionAddress (${address.interfaceName})")
                    Button(onClick = { onCopy(connectionAddress) }) { Text("Copy ${address.type.label()}") }
                }
            }
        }
    }
}

private fun NetworkAddressType.label(): String = when (this) {
    NetworkAddressType.LAN -> "LAN"
    NetworkAddressType.TAILSCALE_OR_HEADSCALE -> "Tailscale/Headscale"
    NetworkAddressType.ZEROTIER -> "ZeroTier"
    NetworkAddressType.WIREGUARD_LIKE -> "WireGuard-like"
    NetworkAddressType.OTHER_VPN -> "Other VPN"
}
