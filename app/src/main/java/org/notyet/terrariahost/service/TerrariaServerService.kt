package org.notyet.terrariahost.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.notyet.terrariahost.data.AndroidNetworkInterfaceProvider
import org.notyet.terrariahost.data.ServerEventBus
import org.notyet.terrariahost.data.TerrariaServerRunner
import org.notyet.terrariahost.domain.DefaultTerrariaPort
import org.notyet.terrariahost.R
import org.notyet.terrariahost.domain.LocalAddressProvider
import org.notyet.terrariahost.domain.network.NetworkDetector

class TerrariaServerService : Service() {
    private lateinit var runner: TerrariaServerRunner
    private val addressProvider = LocalAddressProvider()
    private val networkDetector = NetworkDetector(AndroidNetworkInterfaceProvider())

    override fun onCreate() {
        super.onCreate()
        runner = TerrariaServerRunner(this)
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startServer()
            ACTION_STOP -> stopServer()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startServer() {
        val localAddress = addressProvider.getLanIpv4Address()
        val networkAddresses = networkDetector.detect()
        startForeground(NOTIFICATION_ID, notification(localAddress))
        ServerEventBus.update {
            it.copy(
                localAddress = localAddress,
                port = DefaultTerrariaPort,
                expectedExecutablePath = runner.executable.absolutePath,
                networkAddresses = networkAddresses,
            )
        }
        val started = runner.start(DefaultTerrariaPort) { ServerEventBus.log(it) }
        ServerEventBus.update { it.copy(isRunning = started, localAddress = localAddress) }
    }

    private fun stopServer() {
        runner.stop { ServerEventBus.log(it) }
        ServerEventBus.update { it.copy(isRunning = false) }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun notification(localAddress: String): Notification {
        val stopIntent = PendingIntent.getService(
            this,
            0,
            stopIntent(this),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("LAN server: $localAddress:$DefaultTerrariaPort")
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", stopIntent)
            .build()
    }

    private fun createChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Terraria server", NotificationManager.IMPORTANCE_LOW))
    }

    companion object {
        private const val CHANNEL_ID = "terraria_server"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "org.notyet.terrariahost.START"
        const val ACTION_STOP = "org.notyet.terrariahost.STOP"

        fun startIntent(context: Context) = Intent(context, TerrariaServerService::class.java).setAction(ACTION_START)
        fun stopIntent(context: Context) = Intent(context, TerrariaServerService::class.java).setAction(ACTION_STOP)
    }
}
