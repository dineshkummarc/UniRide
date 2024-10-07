package com.drdisagree.uniride.data.api.models

data class DirectionRoute(
    val overview_polyline: Polyline,
    val legs: List<Leg>
)