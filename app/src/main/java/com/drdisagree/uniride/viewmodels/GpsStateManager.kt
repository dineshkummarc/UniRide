package com.drdisagree.uniride.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GpsStateManager : ViewModel() {

    // a workaround to fix a bug where gps is again requested after log out, so we keep track of it
    private val _gpsRequested = MutableStateFlow(false)
    val gpsRequested: StateFlow<Boolean> = _gpsRequested

    fun setGpsRequested(isRequested: Boolean) {
        _gpsRequested.value = isRequested
    }
}