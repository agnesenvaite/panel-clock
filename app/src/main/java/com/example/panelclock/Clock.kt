package com.example.panelclock

import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.os.Bundle


class Clock : ComponentActivity() {

    private lateinit var clockTextView: TextView
    private  lateinit var dateTextView: TextView
    private var timeJob: Job? = null
    private var clockFormat = DateTimeFormatter.ofPattern("HH:mm")
    private var dateFormat = DateTimeFormatter.ofPattern("EEE, MMMM d")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clock_activity)

        clockTextView = findViewById(R.id.clock)
        dateTextView = findViewById(R.id.date)
        // Start the handler
        updateTime()
    }


    // Method to update the time every second
    private fun updateTime() {
        // Update the TextView with the current time

        timeJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) { // Loop while the coroutine is active
                clockTextView.text = LocalDateTime.now().format(clockFormat)
                dateTextView.text = LocalDateTime.now().format(dateFormat)

                delay(30000)  // Wait for 30 second
            }
        }
    }


    // Cancel the handler to avoid memory leaks when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        timeJob?.cancel()
    }

    }