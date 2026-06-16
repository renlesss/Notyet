package org.notyet.terrariahost.data

import android.content.Context
import org.notyet.terrariahost.domain.DefaultTerrariaPort
import org.notyet.terrariahost.domain.server.ServerInstallChecker
import org.notyet.terrariahost.domain.server.ServerInstallStatus
import java.io.File

class TerrariaServerRunner(
    private val context: Context,
    private val installChecker: ServerInstallChecker = ServerInstallChecker(),
) {
    private var process: Process? = null

    val executable: File
        get() = File(context.filesDir, SERVER_EXECUTABLE_RELATIVE_PATH)

    fun installStatus(): ServerInstallStatus = installChecker.check(executable)

    fun start(port: Int = DefaultTerrariaPort, onLog: (String) -> Unit): Boolean {
        if (process?.isAlive == true) {
            onLog("Server is already running.")
            return true
        }

        when (val status = installStatus()) {
            is ServerInstallStatus.Missing -> {
                onLog("No Terraria server executable found at: ${status.expectedPath}")
                logLegalInstallInstructions(onLog)
                return false
            }
            is ServerInstallStatus.NotExecutable -> {
                onLog("Terraria server file exists but is not executable: ${status.expectedPath}")
                onLog("Mark the user-provided server launcher executable before starting.")
                return false
            }
            is ServerInstallStatus.Ready -> onLog("Using user-provided server launcher: ${status.executablePath}")
        }

        val builder = ProcessBuilder(executable.absolutePath, "-port", port.toString())
            .directory(executable.parentFile)
            .redirectErrorStream(true)

        process = builder.start()
        onLog("Started Terraria server on port $port.")
        Thread({
            process?.inputStream?.bufferedReader()?.useLines { lines ->
                lines.forEach { onLog(it) }
            }
        }, "TerrariaServerLogReader").apply { isDaemon = true }.start()
        return true
    }

    fun stop(onLog: (String) -> Unit) {
        process?.destroy()
        process = null
        onLog("Server stopped.")
    }

    private fun logLegalInstallInstructions(onLog: (String) -> Unit) {
        onLog("Notyet Terraria Host does not ship copyrighted Terraria server files.")
        onLog("Place your legally obtained server launcher at app files path: $SERVER_EXECUTABLE_RELATIVE_PATH")
        onLog("v0.1 is LAN-only; friends must join from the same Wi‑Fi/LAN using the displayed address.")
    }

    companion object {
        const val SERVER_EXECUTABLE_RELATIVE_PATH = "server/TerrariaServer"
    }
}
