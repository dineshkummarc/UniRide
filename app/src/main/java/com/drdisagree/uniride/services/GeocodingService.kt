package com.drdisagree.uniride.services

import com.drdisagree.uniride.data.models.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("geocode/json")
    suspend fun getLocationName(
        @Query("latlng") latlng: String,
        @Query("sensor") sensor: Boolean,
        @Query("key") apiKey: String
    ): GeocodingResponse
}
