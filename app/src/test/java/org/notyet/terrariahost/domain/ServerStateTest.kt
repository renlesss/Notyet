package org.notyet.terrariahost.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ServerStateTest {
    @Test
    fun connectionAddressCombinesIpAndPort() {
        val state = ServerState(localAddress = "192.168.1.10", port = 7777)
        assertEquals("192.168.1.10:7777", state.connectionAddress)
    }
}
