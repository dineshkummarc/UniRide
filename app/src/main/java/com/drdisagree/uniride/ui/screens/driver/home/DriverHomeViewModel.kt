package com.drdisagree.uniride.ui.screens.driver.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.BusStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.data.utils.Constant.RUNNING_BUS_COLLECTION
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

    fun checkIfAnyBusAssignedToDriver(
        driver: Driver?,
        onResult: (Boolean) -> Unit
    ) {
        if (driver == null) {
            onResult(false)
            return
        }

        firestore.collection(RUNNING_BUS_COLLECTION)
            .whereEqualTo("driver.id", driver.id)
            .whereNotEqualTo("status", BusStatus.STOPPED)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val isAssigned = !querySnapshot.isEmpty
                onResult(isAssigned)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun startDeparture(
        driverViewModel: GetDriverViewModel,
        listsViewModel: ListsViewModel,
        busName: String,
        fromPlaceName: String,
        toPlaceName: String,
        categoryName: String
    ) {
        viewModelScope.launch {
            _updateBusStatus.emit(
                Resource.Loading()
            )

            driverViewModel.getDriver.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val driver = resource.data

                        checkIfAnyBusAssignedToDriver(driver) { isAssigned ->
                            if (isAssigned) {
                                viewModelScope.launch {
                                    _updateBusStatus.emit(
                                        Resource.Error("Driver already assigned to a bus")
                                    )
                                }

                                return@checkIfAnyBusAssignedToDriver
                            } else {
                                val bus =
                                    listsViewModel.busModels.value.firstOrNull { it.name == busName }
                                val busCategory =
                                    listsViewModel.busCategoryModels.value.firstOrNull { it.name == categoryName }
                                val fromPlace =
                                    listsViewModel.placeModels.value.firstOrNull { it.name == fromPlaceName }
                                val toPlace =
                                    listsViewModel.placeModels.value.firstOrNull { it.name == toPlaceName }

                                if (bus != null && fromPlace != null && toPlace != null) {
                                    firestore.collection(RUNNING_BUS_COLLECTION)
                                        .document(bus.uuid)
                                        .get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            val existingBus =
                                                documentSnapshot.toObject(RunningBus::class.java)
                                            val shouldAssignNewDriver = existingBus?.driver == null

                                            if (shouldAssignNewDriver) {
                                                val runningBus = RunningBus(
                                                    bus = bus,
                                                    category = busCategory,
                                                    driver = driver,
                                                    status = BusStatus.STANDBY,
                                                    departedFrom = fromPlace,
                                                    departedTo = toPlace,
                                                    departedAt = System.currentTimeMillis(),
                                                    reachedAt = null,
                                                    currentlyAt = null
                                                )

                                                firestore.collection(RUNNING_BUS_COLLECTION)
                                                    .document(bus.uuid)
                                                    .set(runningBus)
                                                    .addOnSuccessListener {
                                                        viewModelScope.launch {
                                                            _updateBusStatus.emit(
                                                                Resource.Success(Unit)
                                                            )
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        viewModelScope.launch {
                                                            _updateBusStatus.emit(
                                                                Resource.Error(it.message.toString())
                                                            )
                                                        }
                                                    }
                                            } else {
                                                viewModelScope.launch {
                                                    _updateBusStatus.emit(
                                                        Resource.Error("Bus already has an assigned driver")
                                                    )
                                                }
                                            }
                                        }
                                        .addOnFailureListener {
                                            viewModelScope.launch {
                                                _updateBusStatus.emit(
                                                    Resource.Error(it.message.toString())
                                                )
                                            }
                                        }
                                } else {
                                    val errorMessage = when {
                                        driver == null -> "Driver not found for ${firebaseAuth.currentUser?.uid}"
                                        bus == null -> "Bus not found for $busName"
                                        fromPlace == null -> "Departure place not found for $fromPlaceName"
                                        else -> "Destination place not found for $toPlaceName"
                                    }

                                    viewModelScope.launch {
                                        _updateBusStatus.emit(
                                            Resource.Error(errorMessage)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        _updateBusStatus.emit(
                            Resource.Error("Driver not found")
                        )
                    }

                    else -> {
                        _updateBusStatus.emit(
                            Resource.Unspecified()
                        )
                    }
                }
            }
        }
    }
}