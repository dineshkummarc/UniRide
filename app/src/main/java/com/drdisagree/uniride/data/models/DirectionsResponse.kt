package com.drdisagree.uniride.data.models

data class DirectionsResponse(
    val routes: List<DirectionRoute>
)

data class DirectionRoute(
    val overview_polyline: Polyline
)

data class Polyline(
    val points: String
)