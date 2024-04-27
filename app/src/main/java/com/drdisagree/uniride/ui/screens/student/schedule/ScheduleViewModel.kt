package com.drdisagree.uniride.ui.screens.student.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import com.drdisagree.uniride.domain.models.Schedule
import com.drdisagree.uniride.domain.repository.ScheduleRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allSchedules = MutableStateFlow<Resource<List<Schedule>>>(Resource.Unspecified())
    val allSchedules = _allSchedules.asStateFlow()

    init {
        getAllSchedules()
    }

    private fun getAllSchedules() {
        viewModelScope.launch {
            _allSchedules.emit(Resource.Loading())
        }

        firestore.collection(SCHEDULE_COLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _allSchedules.emit(
                        Resource.Success(
                            it.toObjects(
                                Schedule::class.java
                            )
                        )
                    )
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _allSchedules.emit(
                        Resource.Error(
                            it.message.toString()
                        )
                    )
                }
            }

        firestore.collection(SCHEDULE_COLLECTION)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _allSchedules.emit(
                            Resource.Error(
                                error.message.toString()
                            )
                        )
                    }
                } else {
                    value?.let {
                        viewModelScope.launch {
                            _allSchedules.emit(
                                Resource.Success(
                                    it.toObjects(
                                        Schedule::class.java
                                    )
                                )
                            )
                        }
                    }
                }
            }
    }
}