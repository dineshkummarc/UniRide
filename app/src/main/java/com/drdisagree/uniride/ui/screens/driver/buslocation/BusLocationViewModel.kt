package com.drdisagree.uniride.ui.screens.driver.buslocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.utils.Constant.BUS_COLLECTION
import com.drdisagree.uniride.ui.screens.global.viewmodels.ListsViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusLocationViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _updateBusLocation = MutableSharedFlow<Resource<Unit>>()
    val updateBusLocation = _updateBusLocation.asSharedFlow()

    fun updateBusLocation(
        listsViewModel: ListsViewModel,
        location: LatLng
    ) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            viewModelScope.launch {
                _updateBusLocation.emit(Resource.Error("User not authenticated"))
            }
            return
        }

        viewModelScope.launch {
            val bus = listsViewModel.busModels.value.firstOrNull { it.driver?.id == userId }

            if (bus != null) {
                val updatedBus = bus.copy(currentlyAt = location)

                firestore.collection(BUS_COLLECTION)
                    .document(bus.uuid)
                    .set(updatedBus)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            _updateBusLocation.emit(Resource.Success(Unit))
                        }
                    }
                    .addOnFailureListener { exception ->
                        viewModelScope.launch {
                            _updateBusLocation.emit(Resource.Error(exception.message.toString()))
                        }
                    }
            } else {
                viewModelScope.launch {
                    _updateBusLocation.emit(Resource.Error("Bus not found for the current driver id"))
                }
            }
        }
    }
}