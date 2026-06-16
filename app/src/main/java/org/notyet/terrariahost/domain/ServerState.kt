package org.notyet.terrariahost.domain

import org.notyet.terrariahost.domain.network.NetworkAddress

const val DefaultTerrariaPort: Int = 7777

data class ServerState(
    val isRunning: Boolean = false,
    val localAddress: String = "0.0.0.0",
    val port: Int = DefaultTerrariaPort,
    val expectedExecutablePath: String = "server/TerrariaServer",
    val networkAddresses: List<NetworkAddress> = emptyList(),
    val logs: List<String> = emptyList(),
) {
    val connectionAddress: String get() = "$localAddress:$port"
}
