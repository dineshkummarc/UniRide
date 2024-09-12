package com.drdisagree.uniride.utils.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Bus
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.data.utils.Constant.BUS_CATEGORY_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.BUS_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.PLACE_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.RUNNING_BUS_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<String>>()
    val state = _state.asSharedFlow()

    private val _busModels = MutableStateFlow<List<Bus>>(emptyList())
    val busModels: StateFlow<List<Bus>> = _busModels

    private val _runningBusModels = MutableStateFlow<List<RunningBus>>(emptyList())
    val runningBusModels: StateFlow<List<RunningBus>> = _runningBusModels

    private val _busCategoryModels = MutableStateFlow<List<BusCategory>>(emptyList())
    val busCategoryModels: StateFlow<List<BusCategory>> = _busCategoryModels

    private val _placeModels = MutableStateFlow<List<Place>>(emptyList())
    val placeModels: StateFlow<List<Place>> = _placeModels

    init {
        getBusList()
        getRunningBusList()
        getBusCategoryList()
        getPlaceList()
    }

    private fun getBusList() {
        firestore.collection(BUS_COLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val models = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Bus::class.java)?.copy(uuid = doc.id)
                }.sortedWith(compareBy { it.name })

                _busModels.value = models
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun getRunningBusList() {
        firestore.collection(RUNNING_BUS_COLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val models = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(RunningBus::class.java)?.copy(uuid = doc.id)
                }.sortedWith(compareBy { it.bus.name })

                _runningBusModels.value = models
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun getBusCategoryList() {
        firestore.collection(BUS_CATEGORY_COLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val models = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(BusCategory::class.java)?.copy(uuid = doc.id)
                }.sortedWith(compareBy { it.name })

                _busCategoryModels.value = models
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun getPlaceList() {
        firestore.collection(PLACE_COLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val models = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Place::class.java)?.copy(uuid = doc.id)
                }.sortedWith(compareBy { it.name })

                _placeModels.value = models
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}