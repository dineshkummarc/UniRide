package com.drdisagree.uniride.utils.repositories

import com.drdisagree.uniride.BuildConfig
import com.drdisagree.uniride.services.GeocodingService
import javax.inject.Inject

class GeocodingRepository @Inject constructor(
    private val geocodingService: GeocodingService
) {

    suspend fun getLocationName(lat: Double, lng: Double): String? {
        val response = geocodingService.getLocationName("$lat,$lng", true, BuildConfig.MAPS_API_KEY)

        if (response.status == "OK" && response.results.isNotEmpty()) {
            response.results.forEach { result ->
                result.addressComponents.forEach { component ->
                    if (component.types.contains("sublocality")) {
                        return component.shortName
                    }
                }
            }
        }
        return null
    }
}