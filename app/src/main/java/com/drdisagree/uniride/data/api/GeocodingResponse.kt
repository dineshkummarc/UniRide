package com.drdisagree.uniride.data.api

import com.drdisagree.uniride.data.api.models.GeocodingResult

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)