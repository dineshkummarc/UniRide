package com.drdisagree.uniride.utils

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

@Composable
fun sensorRotationEffect(context: Context): Int {
    val sensorManager = remember { context.getSystemService(SENSOR_SERVICE) as SensorManager }
    val isMagneticFieldSensorPresent = remember {
        sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD) != null
    }
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)
    val rotationMatrix = FloatArray(9)
    val mOrientationAngles = FloatArray(3)
    var degrees by rememberSaveable { mutableIntStateOf(0) }
    var currentTime by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == TYPE_ACCELEROMETER) {
                    System.arraycopy(
                        event.values,
                        0,
                        accelerometerReading,
                        0,
                        accelerometerReading.size
                    )
                } else if (event.sensor.type == TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(
                        event.values,
                        0,
                        magnetometerReading,
                        0,
                        magnetometerReading.size
                    )
                }
                val azimuthInRadians = mOrientationAngles[0]

                val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).roundToInt()

                if (currentTime + 1000 < System.currentTimeMillis()) {
                    degrees = if (azimuthInDegrees < 0) {
                        azimuthInDegrees + 360
                    } else {
                        azimuthInDegrees
                    }
                    currentTime = System.currentTimeMillis()
                }

                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )

                SensorManager.getOrientation(rotationMatrix, mOrientationAngles)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        if (isMagneticFieldSensorPresent) {
            sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)?.also { accelerometer ->
                sensorManager.registerListener(
                    sensorEventListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
            sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)?.also { magneticField ->
                sensorManager.registerListener(
                    sensorEventListener,
                    magneticField,
                    SensorManager.SENSOR_DELAY_GAME,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
        }

        onDispose {
            if (isMagneticFieldSensorPresent) {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
    }

    return degrees
}