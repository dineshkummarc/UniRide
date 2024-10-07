package com.drdisagree.uniride.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geocode/json")
    suspend fun getLocationName(
        @Query("latlng") latlng: String,
        @Query("sensor") sensor: Boolean,
        @Query("key") apiKey: String
    ): GeocodingResponse
}
