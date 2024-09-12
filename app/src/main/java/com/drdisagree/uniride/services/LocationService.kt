package com.drdisagree.uniride.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.drdisagree.uniride.R
import com.drdisagree.uniride.ui.activities.MainActivity
import com.drdisagree.uniride.utils.viewmodels.LocationSharingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService() : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationViewModel: LocationSharingViewModel
    private var locationJob: Job? = null
    private val notificationId = 99
    private var running = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        locationViewModel = LocationSharingViewModel(application)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        if (running) return

        val intent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification
            .Builder(this, "location")
            .setContentTitle("Tracking location")
            .setContentText("Location: Searching for location...")
            .setSmallIcon(R.drawable.ic_launcher_icon)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        locationJob?.cancel()

        locationJob = locationViewModel.locationFlow
            .onEach { location ->
                if (!running) return@onEach

                val updatedNotification = notification.setContentText(
                    "Latitude: ${location?.latitude}\nLongitude: ${location?.longitude}"
                )
                notificationManager.notify(notificationId, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(notificationId, notification.build())

        running = true
    }

    private fun stop() {
        if (!running) return

        locationJob?.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        running = false
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        running = false
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
