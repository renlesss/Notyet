package org.notyet.terrariahost.domain.network

class NetworkDetector(
    private val provider: NetworkInterfaceProvider,
) {
    fun detect(): List<NetworkAddress> = provider.interfaces()
        .asSequence()
        .filter { it.isUp && !it.isLoopback }
        .flatMap { networkInterface ->
            networkInterface.addresses.asSequence()
                .filter(::isUsableIpv4)
                .map { address ->
                    NetworkAddress(
                        interfaceName = networkInterface.name,
                        hostAddress = address,
                        type = classify(networkInterface.name, address, networkInterface.isVirtual),
                    )
                }
        }
        .distinctBy { it.hostAddress }
        .sortedWith(compareBy<NetworkAddress> { it.type.ordinal }.thenBy { it.interfaceName }.thenBy { it.hostAddress })
        .toList()

    private fun classify(interfaceName: String, address: String, isVirtual: Boolean): NetworkAddressType {
        val normalizedName = interfaceName.lowercase()
        return when {
            isTailscaleOrHeadscale(address) -> NetworkAddressType.TAILSCALE_OR_HEADSCALE
            address.startsWith("10.147.") || normalizedName.contains("zt") || normalizedName.contains("zerotier") -> NetworkAddressType.ZEROTIER
            normalizedName.startsWith("wg") || normalizedName.contains("wireguard") -> NetworkAddressType.WIREGUARD_LIKE
            isPrivateLan(address) && !isVirtual && !looksLikeVpnName(normalizedName) -> NetworkAddressType.LAN
            isVirtual || looksLikeVpnName(normalizedName) -> NetworkAddressType.OTHER_VPN
            else -> NetworkAddressType.LAN
        }
    }

    private fun isUsableIpv4(address: String): Boolean = IPV4_REGEX.matches(address) &&
        !address.startsWith("127.") &&
        !address.startsWith("169.254.") &&
        address != "0.0.0.0"

    private fun isTailscaleOrHeadscale(address: String): Boolean {
        val parts = address.split(".").mapNotNull { it.toIntOrNull() }
        return parts.size == 4 && parts[0] == 100 && parts[1] in 64..127
    }

    private fun isPrivateLan(address: String): Boolean = address.startsWith("192.168.") ||
        address.startsWith("172.16.") || address.startsWith("172.17.") || address.startsWith("172.18.") ||
        address.startsWith("172.19.") || address.startsWith("172.20.") || address.startsWith("172.21.") ||
        address.startsWith("172.22.") || address.startsWith("172.23.") || address.startsWith("172.24.") ||
        address.startsWith("172.25.") || address.startsWith("172.26.") || address.startsWith("172.27.") ||
        address.startsWith("172.28.") || address.startsWith("172.29.") || address.startsWith("172.30.") ||
        address.startsWith("172.31.") || address.startsWith("10.")

    private fun looksLikeVpnName(name: String): Boolean = name.contains("tun") ||
        name.contains("tap") ||
        name.contains("vpn") ||
        name.startsWith("wg") ||
        name.contains("wireguard") ||
        name.contains("tailscale") ||
        name.contains("zerotier") ||
        name.contains("zt")

    companion object {
        private val IPV4_REGEX = Regex("^(25[0-5]|2[0-4]\\d|1?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|1?\\d?\\d)){3}$")
    }
}
