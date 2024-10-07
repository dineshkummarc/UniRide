package com.drdisagree.uniride.ui.screens.student.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.enums.BusStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.data.utils.Constant.RUNNING_BUS_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyBusesViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<Unit>>()
    val state = _state.asSharedFlow()

    private val _runningBuses = MutableLiveData<List<RunningBus>>()
    val runningBuses: LiveData<List<RunningBus>> = _runningBuses

    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        if (auth.currentUser == null) {
            _runningBuses.postValue(emptyList())
        }

        viewModelScope.launch {
            _state.emit(
                Resource.Loading()
            )
        }

        listenerRegistration = firestore.collection(RUNNING_BUS_COLLECTION)
            .whereNotEqualTo("status", BusStatus.STOPPED)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _runningBuses.postValue(emptyList())
                    viewModelScope.launch {
                        _state.emit(
                            Resource.Error(exception.message.toString())
                        )
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val buses = snapshot.documents.mapNotNull { document ->
                        document.toObject(RunningBus::class.java)
                    }.filter { runningBus ->
                        runningBus.driver != null
                    }

                    _runningBuses.postValue(buses)
                    viewModelScope.launch {
                        delay(500)
                        _state.emit(
                            Resource.Success(Unit)
                        )
                    }
                } else {
                    _runningBuses.postValue(emptyList())
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}