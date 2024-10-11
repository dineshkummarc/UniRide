package com.drdisagree.uniride.data.repositories

import com.drdisagree.uniride.data.api.GeocodingApi
import com.drdisagree.uniride.data.api.Keys
import javax.inject.Inject

class GeocodingRepository @Inject constructor(
    private val geocodingService: GeocodingApi
) {

    suspend fun getLocationName(lat: Double, lng: Double): String? {
        val response = geocodingService.getLocationName("$lat,$lng", true, Keys.mapsApiKey())

        if (response.status == "OK" && response.results.isNotEmpty()) {
            response.results.forEach { result ->
                result.addressComponents.forEach { component ->
                    if (component.types.contains("sublocality")) {
                        return component.shortName
                    } else if (component.types.contains("locality")) {
                        return component.shortName
                    }
                }
            }
        }
        return null
    }
}