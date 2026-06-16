package org.notyet.terrariahost.domain.network

import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkDetectorTest {
    @Test
    fun detectClassifiesLanTailscaleZeroTierAndWireGuardLikeAddresses() {
        val detector = NetworkDetector(
            NetworkInterfaceProvider {
                listOf(
                    NetworkInterfaceSnapshot("wlan0", isUp = true, isLoopback = false, isVirtual = false, addresses = listOf("192.168.1.20")),
                    NetworkInterfaceSnapshot("tailscale0", isUp = true, isLoopback = false, isVirtual = true, addresses = listOf("100.101.102.103")),
                    NetworkInterfaceSnapshot("ztabc123", isUp = true, isLoopback = false, isVirtual = true, addresses = listOf("10.147.20.3")),
                    NetworkInterfaceSnapshot("wg0", isUp = true, isLoopback = false, isVirtual = true, addresses = listOf("10.8.0.2")),
                )
            },
        )

        val byAddress = detector.detect().associateBy { it.hostAddress }

        assertEquals(NetworkAddressType.LAN, byAddress.getValue("192.168.1.20").type)
        assertEquals(NetworkAddressType.TAILSCALE_OR_HEADSCALE, byAddress.getValue("100.101.102.103").type)
        assertEquals(NetworkAddressType.ZEROTIER, byAddress.getValue("10.147.20.3").type)
        assertEquals(NetworkAddressType.WIREGUARD_LIKE, byAddress.getValue("10.8.0.2").type)
    }

    @Test
    fun detectIgnoresLoopbackLinkLocalAndInvalidAddresses() {
        val detector = NetworkDetector(
            NetworkInterfaceProvider {
                listOf(
                    NetworkInterfaceSnapshot("lo", isUp = true, isLoopback = true, isVirtual = false, addresses = listOf("127.0.0.1")),
                    NetworkInterfaceSnapshot("wlan0", isUp = true, isLoopback = false, isVirtual = false, addresses = listOf("169.254.1.2", "not-an-ip")),
                )
            },
        )

        assertEquals(emptyList<NetworkAddress>(), detector.detect())
    }
}
