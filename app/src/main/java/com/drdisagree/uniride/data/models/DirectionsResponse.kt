package com.drdisagree.uniride.data.models

data class DirectionsResponse(
    val routes: List<DirectionRoute>
)

data class DirectionRoute(
    val overview_polyline: Polyline,
    val legs: List<Leg>
)

data class Leg(
    val distance: Distance
)

data class Distance(
    val text: String,
    val value: Int // Distance in meters
)

data class Polyline(
    val points: String
)