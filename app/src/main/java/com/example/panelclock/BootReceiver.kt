package com.example.panelclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Start the main activity
            val startIntent = Intent(context, MainActivity::class.java)
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(startIntent)
            context?.let {
                bringAppToFront(it)
            }
        }
    }

    fun bringAppToFront(context: Context) {
        val packageName = context.packageName
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            // Flags to bring the app to the foreground if it's running
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)

            // Start the main activity to bring the app to the front
            context.startActivity(launchIntent)
        }
    }
}