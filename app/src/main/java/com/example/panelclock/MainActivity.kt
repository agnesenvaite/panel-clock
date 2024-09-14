package com.example.panelclock

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Job


class MainActivity : ComponentActivity() {

    private lateinit var clockTextView: TextView
    private  lateinit var dateTextView: TextView
    private var timeJob: Job? = null
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var isScreenOn = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        checkAndRequestIgnoreBatteryOptimizations(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize sensor manager and proximity sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        sensorManager.registerListener(
            proximitySensorEventListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun checkAndRequestIgnoreBatteryOptimizations(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = context.packageName

        // Check if the app is already ignoring battery optimizations
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                // If not, prompt the user to ignore battery optimizations
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                context.startActivity(intent)
                return
            }

            if (!isAccessibilityServiceEnabled(this, GlobalTouchService::class.java)) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
                return
            }
                val intent = Intent(this, Clock::class.java)

                startActivity(intent)

        }
    }


    fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val colonSplitter = TextUtils.SimpleStringSplitter('/')
        colonSplitter.setString(enabledServices)
        val colonSplitterIterator = colonSplitter.iterator()

        while (colonSplitterIterator.hasNext()) {
            val componentName = colonSplitterIterator.next()
            if (componentName.equals(service.name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }


    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // method to check accuracy changed in sensor.
        }

        override fun onSensorChanged(event: SensorEvent) {
            // check if the sensor type is proximity sensor.
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] > 2000) {
                    // here we are setting our status to our textview..
                    // if sensor event return 0 then object is closed
                    // to sensor else object is away from sensor.
                    if (isScreenOn) {
                        // Open home assistant
                        val launchIntent = packageManager.getLaunchIntentForPackage("io.homeassistant.companion.android")
                        startActivity(launchIntent)
                        // Minimize the app
                        moveTaskToBack(true)
                    }
                }
            }
        }
    }
}