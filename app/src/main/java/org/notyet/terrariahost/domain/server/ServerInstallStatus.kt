package org.notyet.terrariahost.domain.server

sealed interface ServerInstallStatus {
    data class Ready(val executablePath: String) : ServerInstallStatus
    data class Missing(val expectedPath: String) : ServerInstallStatus
    data class NotExecutable(val expectedPath: String) : ServerInstallStatus
}
