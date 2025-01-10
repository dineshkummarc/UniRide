package com.drdisagree.uniride.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MathUtils {

    fun calculateDegrees(startPoint: LatLng, endPoint: LatLng): Double {
        val startLat = toRadians(startPoint.latitude)
        val startLng = toRadians(startPoint.longitude)
        val endLat = toRadians(endPoint.latitude)
        val endLng = toRadians(endPoint.longitude)

        val deltaLng = endLng - startLng

        val y = sin(deltaLng) * cos(endLat)
        val x = cos(startLat) * sin(endLat) - sin(startLat) * cos(endLat) * cos(deltaLng)

        val bearing = atan2(y, x)

        return (toDegrees(bearing) + 360) % 360
    }

    private fun toRadians(degrees: Double) = degrees * (Math.PI / 180.0)

    private fun toDegrees(radians: Double) = radians * (180.0 / Math.PI)
}