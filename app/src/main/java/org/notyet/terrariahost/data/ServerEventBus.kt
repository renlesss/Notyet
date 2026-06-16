package org.notyet.terrariahost.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.notyet.terrariahost.domain.ServerState

object ServerEventBus {
    private val _state = MutableStateFlow(ServerState())
    val state: StateFlow<ServerState> = _state

    fun update(transform: (ServerState) -> ServerState) {
        _state.value = transform(_state.value)
    }

    fun log(message: String) = update { current ->
        current.copy(logs = (current.logs + message).takeLast(250))
    }
}
