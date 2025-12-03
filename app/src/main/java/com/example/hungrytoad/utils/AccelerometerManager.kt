package com.example.hungrytoad.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AccelerometerManager(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val _tilt = MutableStateFlow(Pair(0f, 0f))
    val tilt: StateFlow<Pair<Float, Float>> = _tilt
    private var isRunning = false
    fun start() {
        if (!isRunning) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            isRunning = true
        }
    }
    fun stop() {
        if (isRunning) {
            sensorManager.unregisterListener(this)
            isRunning = false
            _tilt.value = Pair(0f, 0f)
        }
    }
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && isRunning) {
            val x = event.values[0]
            val y = event.values[1]

            val filteredX = if (Math.abs(x) < 0.3f) 0f else x
            val filteredY = if (Math.abs(y) < 0.3f) 0f else y

            _tilt.value = Pair(filteredX, filteredY)
        }
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}