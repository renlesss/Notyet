package org.notyet.terrariahost.domain.network

enum class NetworkAddressType {
    LAN,
    TAILSCALE_OR_HEADSCALE,
    ZEROTIER,
    WIREGUARD_LIKE,
    OTHER_VPN,
}

data class NetworkAddress(
    val interfaceName: String,
    val hostAddress: String,
    val type: NetworkAddressType,
) {
    fun connectionAddress(port: Int): String = "$hostAddress:$port"
}
