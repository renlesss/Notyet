package org.notyet.terrariahost.domain

import org.notyet.terrariahost.domain.network.Ipv4AddressSelector
import java.net.NetworkInterface

class LocalAddressProvider(
    private val selector: Ipv4AddressSelector = Ipv4AddressSelector(),
) {
    fun getLanIpv4Address(): String {
        val addresses = NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { it.isUp && !it.isLoopback && !it.isVirtual }
            .flatMap { it.inetAddresses.asSequence() }
        return selector.select(addresses)
    }
}
