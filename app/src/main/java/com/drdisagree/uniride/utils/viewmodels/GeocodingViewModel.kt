package com.drdisagree.uniride.utils.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.utils.DistanceUtils.distance
import com.drdisagree.uniride.utils.repositories.GeocodingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class GeocodingViewModel @Inject constructor(
    private val repository: GeocodingRepository
) : ViewModel() {

    private val _locationNames = MutableLiveData<Map<String, String>>(emptyMap())
    val locationNames: LiveData<Map<String, String>> = _locationNames

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: MutableLiveData<String> = _errorMessage

    private val lastCheckedTime = ConcurrentHashMap<String, Long>()
    private val lastKnownLocation = ConcurrentHashMap<String, Pair<Double, Double>>()
    private val checkIntervalMillis = 2 * 60 * 1000L // 2 minutes
    private val distanceThreshold = 1.0 // distance in kilometers

    fun fetchLocationName(uuid: String, lat: Double?, lng: Double?) {
        val currentTime = System.currentTimeMillis()
        val lastChecked = lastCheckedTime[uuid] ?: 0L

        if (lat == null || lng == null) {
            errorMessage.postValue("Lat and Lng cannot be null")
            return
        }

        val lastLocation = lastKnownLocation[uuid]
        val shouldFetch = lastLocation == null ||
                distance(lat, lng, lastLocation.first, lastLocation.second) >= distanceThreshold ||
                currentTime - lastChecked >= checkIntervalMillis

        if (!shouldFetch) return

        viewModelScope.launch {
            try {
                val name = repository.getLocationName(lat, lng)

                if (name == null) {
                    errorMessage.postValue("Location not found")
                    lastCheckedTime.remove(uuid)
                    return@launch
                }

                updateLocationName(uuid, name)
                lastCheckedTime[uuid] = currentTime
                lastKnownLocation[uuid] = Pair(lat, lng)
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
                lastCheckedTime.remove(uuid)
                lastKnownLocation.remove(uuid)
            }
        }
    }

    private fun updateLocationName(uuid: String, name: String) {
        val currentNames = _locationNames.value ?: emptyMap()
        _locationNames.postValue(currentNames + (uuid to name))
    }
}