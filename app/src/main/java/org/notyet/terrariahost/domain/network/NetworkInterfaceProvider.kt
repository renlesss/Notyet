package org.notyet.terrariahost.domain.network

fun interface NetworkInterfaceProvider {
    fun interfaces(): List<NetworkInterfaceSnapshot>
}
