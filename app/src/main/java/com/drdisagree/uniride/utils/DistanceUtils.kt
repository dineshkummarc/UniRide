package com.drdisagree.uniride.utils

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

object DistanceUtils {

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of the Earth in kilometers
        val p = Math.PI / 180.0 // Conversion factor from degrees to radians

        val a = 0.5 - cos((lat2 - lat1) * p) / 2 +
                cos(lat1 * p) * cos(lat2 * p) *
                (1 - cos((lon2 - lon1) * p)) / 2

        return 2 * r * asin(sqrt(a))
    }
}