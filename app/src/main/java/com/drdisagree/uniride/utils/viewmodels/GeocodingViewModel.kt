package com.drdisagree.uniride.utils.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.utils.repositories.GeocodingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeocodingViewModel @Inject constructor(
    private val repository: GeocodingRepository
) : ViewModel() {

    private val _locationName = MutableLiveData<String?>()
    val locationName: MutableLiveData<String?> = _locationName

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: MutableLiveData<String> = _errorMessage

    fun fetchLocationName(lat: Double?, lng: Double?) {
        viewModelScope.launch {
            try {
                if (lat == null || lng == null) {
                    errorMessage.postValue("Lat and Lng cannot be null")
                    return@launch
                }

                val name = repository.getLocationName(lat, lng)

                if (name == null) {
                    errorMessage.postValue("Location not found")
                    return@launch
                }

                locationName.postValue(name)
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            }
        }
    }
}