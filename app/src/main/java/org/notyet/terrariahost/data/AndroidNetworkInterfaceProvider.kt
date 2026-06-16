package org.notyet.terrariahost.data

import org.notyet.terrariahost.domain.network.NetworkInterfaceProvider
import org.notyet.terrariahost.domain.network.NetworkInterfaceSnapshot
import java.net.Inet4Address
import java.net.NetworkInterface

class AndroidNetworkInterfaceProvider : NetworkInterfaceProvider {
    override fun interfaces(): List<NetworkInterfaceSnapshot> = NetworkInterface.getNetworkInterfaces().asSequence()
        .map { networkInterface ->
            NetworkInterfaceSnapshot(
                name = networkInterface.name,
                isUp = networkInterface.isUp,
                isLoopback = networkInterface.isLoopback,
                isVirtual = networkInterface.isVirtual,
                addresses = networkInterface.inetAddresses.asSequence()
                    .filterIsInstance<Inet4Address>()
                    .mapNotNull { it.hostAddress }
                    .toList(),
            )
        }
        .toList()
}
