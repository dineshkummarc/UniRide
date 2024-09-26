package com.drdisagree.uniride.ui.screens.admin.schedule.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditScheduleViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _editState = MutableSharedFlow<Resource<Schedule>>()
    val editState = _editState.asSharedFlow()

    private val _deleteState = MutableSharedFlow<Resource<String>>()
    val deleteState = _deleteState.asSharedFlow()

    fun editSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _editState.emit(Resource.Loading())
        }

        firestore.collection(SCHEDULE_COLLECTION)
            .whereEqualTo("uuid", schedule.uuid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    document.reference
                        .set(schedule)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _editState.emit(Resource.Success(schedule))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _editState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _editState.emit(Resource.Error("Schedule with specified id not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _editState.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun deleteSchedule(uuid: String) {
        viewModelScope.launch {
            _deleteState.emit(Resource.Loading())
        }

        firestore.collection(SCHEDULE_COLLECTION)
            .whereEqualTo("uuid", uuid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    document.reference
                        .delete()
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _deleteState.emit(Resource.Success("Schedule deleted successfully"))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _deleteState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _deleteState.emit(Resource.Error("Schedule with specified id not found"))
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _editState.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
}