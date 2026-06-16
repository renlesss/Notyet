package org.notyet.terrariahost.domain.network

import java.net.Inet4Address
import java.net.InetAddress

class Ipv4AddressSelector {
    fun select(addresses: Sequence<InetAddress>): String = addresses
        .filterIsInstance<Inet4Address>()
        .firstOrNull { !it.isLoopbackAddress && !it.isLinkLocalAddress }
        ?.hostAddress
        ?: FALLBACK_ADDRESS

    companion object {
        const val FALLBACK_ADDRESS = "0.0.0.0"
    }
}
