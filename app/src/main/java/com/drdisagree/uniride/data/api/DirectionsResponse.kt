package com.drdisagree.uniride.data.api

import com.drdisagree.uniride.data.api.models.DirectionRoute

data class DirectionsResponse(
    val routes: List<DirectionRoute>
)