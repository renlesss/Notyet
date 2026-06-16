package org.notyet.terrariahost.domain.network

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.InetAddress

class Ipv4AddressSelectorTest {
    private val selector = Ipv4AddressSelector()

    @Test
    fun selectReturnsFirstNonLoopbackLanIpv4() {
        val result = selector.select(
            sequenceOf(
                InetAddress.getByName("127.0.0.1"),
                InetAddress.getByName("169.254.1.10"),
                InetAddress.getByName("192.168.1.50"),
            ),
        )

        assertEquals("192.168.1.50", result)
    }

    @Test
    fun selectFallsBackWhenNoLanIpv4Exists() {
        val result = selector.select(sequenceOf(InetAddress.getByName("::1")))

        assertEquals(Ipv4AddressSelector.FALLBACK_ADDRESS, result)
    }
}
