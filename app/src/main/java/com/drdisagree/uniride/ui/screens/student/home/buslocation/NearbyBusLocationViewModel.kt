package com.drdisagree.uniride.ui.screens.student.home.buslocation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.BuildConfig
import com.drdisagree.uniride.data.api.DirectionsApi
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.data.utils.Constant.RUNNING_BUS_COLLECTION
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.maps.android.PolyUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyBusLocationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val directionsApi: DirectionsApi
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<Unit>>()
    val state = _state.asSharedFlow()

    private val _runningBus = MutableLiveData<RunningBus?>()
    val runningBus: LiveData<RunningBus?> = _runningBus

    private val _routePoints = MutableLiveData<List<LatLng>>()
    val routePoints: LiveData<List<LatLng>> = _routePoints

    private var listenerRegistration: ListenerRegistration? = null

    fun startListening(uuid: String) {
        listenerRegistration?.remove()

        listenerRegistration = firestore.collection(RUNNING_BUS_COLLECTION)
            .whereEqualTo("uuid", uuid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _state.emit(
                            Resource.Error(error.message.toString())
                        )
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents.firstOrNull()
                    val bus = document?.toObject(RunningBus::class.java)
                    _runningBus.postValue(bus)

                    viewModelScope.launch {
                        _state.emit(
                            Resource.Success(Unit)
                        )
                    }
                } else {
                    _runningBus.postValue(null)

                    viewModelScope.launch {
                        _state.emit(
                            Resource.Error("No running bus found with uuid: $uuid")
                        )
                    }
                }
            }
    }

    fun fetchRoute(origin: LatLng, destination: LatLng) {
        viewModelScope.launch {
            try {
                val response = directionsApi.getDirections(
                    origin = "${origin.latitude},${origin.longitude}",
                    destination = "${destination.latitude},${destination.longitude}",
                    apiKey = BuildConfig.MAPS_API_KEY
                )
                if (response.routes.isNotEmpty()) {
                    val polylinePoints = response.routes[0].overview_polyline.points
                    _routePoints.postValue(PolyUtil.decode(polylinePoints))
                }
            } catch (e: Exception) {
                Log.e("NearbyBusLocationViewModel", "fetchRoute: ${e.message}", e)
                _state.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}