package com.example.panelclock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())

        // Perform the service tasks here
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "BOOT_COMPLETED_SERVICE_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Boot Service Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("App Running in Background")
            .setContentText("The app is monitoring boot events.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}