package org.notyet.terrariahost.domain.server

import java.io.File

class ServerInstallChecker {
    fun check(executable: File): ServerInstallStatus = when {
        !executable.exists() -> ServerInstallStatus.Missing(executable.absolutePath)
        !executable.canExecute() -> ServerInstallStatus.NotExecutable(executable.absolutePath)
        else -> ServerInstallStatus.Ready(executable.absolutePath)
    }
}
