package com.drdisagree.uniride.ui.screens.driver.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.BusStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.utils.Constant.BUS_COLLECTION
import com.drdisagree.uniride.ui.screens.global.viewmodels.GetDriverViewModel
import com.drdisagree.uniride.ui.screens.global.viewmodels.ListsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverHomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _updateBusStatus = MutableSharedFlow<Resource<Unit>>()
    val updateBusStatus = _updateBusStatus.asSharedFlow()

    fun startDeparture(
        driverViewModel: GetDriverViewModel,
        listsViewModel: ListsViewModel,
        busName: String,
        fromPlaceName: String,
        toPlaceName: String,
        category: BusCategory
    ) {
        viewModelScope.launch {
            driverViewModel.getDriver.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val driver = resource.data
                        val bus =
                            listsViewModel.busModels.value.firstOrNull { it.name == busName }
                        val fromPlace =
                            listsViewModel.placeModels.value.firstOrNull { it.name == fromPlaceName }
                        val toPlace =
                            listsViewModel.placeModels.value.firstOrNull { it.name == toPlaceName }

                        if (bus != null && fromPlace != null && toPlace != null) {
                            val updatedBus = bus.copy(
                                departedFrom = fromPlace,
                                departedTo = toPlace,
                                departedAt = System.currentTimeMillis(),
                                category = category,
                                driver = driver,
                                status = BusStatus.STANDBY
                            )

                            firestore.collection(BUS_COLLECTION)
                                .document(bus.uuid)
                                .set(updatedBus)
                                .addOnSuccessListener {
                                    viewModelScope.launch {
                                        _updateBusStatus.emit(Resource.Success(Unit))
                                    }
                                }
                                .addOnFailureListener {
                                    viewModelScope.launch {
                                        _updateBusStatus.emit(Resource.Error(it.message.toString()))
                                    }
                                }
                        } else {
                            val errorMessage = when {
                                driver == null -> "Driver not found for ${firebaseAuth.currentUser?.uid}"
                                bus == null -> "Bus not found for $busName"
                                fromPlace == null -> "Departure place not found for $fromPlaceName"
                                else -> "Destination place not found for $toPlaceName"
                            }
                            _updateBusStatus.emit(Resource.Error(errorMessage))
                        }
                    }

                    is Resource.Error -> {
                        _updateBusStatus.emit(Resource.Error("Driver not found"))
                    }

                    else -> {}
                }
            }
        }
    }
}