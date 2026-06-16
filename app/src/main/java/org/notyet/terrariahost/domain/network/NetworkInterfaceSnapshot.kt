package org.notyet.terrariahost.domain.network

data class NetworkInterfaceSnapshot(
    val name: String,
    val isUp: Boolean,
    val isLoopback: Boolean,
    val isVirtual: Boolean,
    val addresses: List<String>,
)
