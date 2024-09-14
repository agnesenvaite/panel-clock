package com.example.panelclock

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date
import java.time.Duration

class GlobalTouchService : AccessibilityService() {

    private var timeJob: Job? = null
    private var lastToucheAt = LocalDateTime.now();

    override fun onServiceConnected() {
        super.onServiceConnected()
        checkInactivity()
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.i("GlobalTouchService", event?.eventType.toString())
        // Monitor all accessibility events
        Log.i("GlobalTouchService", "User touched the screen")
        lastToucheAt = LocalDateTime.now();
    }

    override fun onInterrupt() {
        Log.i("OnIntrreput", "interuppted")
        // Handle interruptions if necessary
    }

    fun checkInactivity() {
        timeJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) { // Loop while the coroutine is active
                var now = LocalDateTime.now();
                val duration = Duration.between(lastToucheAt, now)
                val differenceInMillis = duration.toMillis()

                Log.i("GlobalTouchService", differenceInMillis.toString())

                if (differenceInMillis > 15000) {
                    bringAppToFront("com.example.panelclock")

                }

                delay(30000)  // Wait for 30 second
            }
        }
    }
    private fun bringAppToFront(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(it)
        } ?: run {
            Log.e("GlobalTouchService", "Package not found: $packageName")
        }
    }
}