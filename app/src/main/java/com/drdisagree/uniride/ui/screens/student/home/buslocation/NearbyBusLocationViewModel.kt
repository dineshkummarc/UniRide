package com.drdisagree.uniride.ui.screens.student.home.buslocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.RunningBus
import com.drdisagree.uniride.data.utils.Constant.RUNNING_BUS_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyBusLocationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<Unit>>()
    val state = _state.asSharedFlow()

    private val _runningBus = MutableLiveData<RunningBus?>()
    val runningBus: LiveData<RunningBus?> = _runningBus

    private var listenerRegistration: ListenerRegistration? = null

    fun startListening(uuid: String) {
        listenerRegistration?.remove()

        listenerRegistration = firestore.collection(RUNNING_BUS_COLLECTION)
            .whereEqualTo("uuid", uuid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    viewModelScope.launch {
                        _state.emit(
                            Resource.Error(e.message.toString())
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

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}