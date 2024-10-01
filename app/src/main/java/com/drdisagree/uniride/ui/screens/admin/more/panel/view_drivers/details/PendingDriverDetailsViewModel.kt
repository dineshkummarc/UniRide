package com.drdisagree.uniride.ui.screens.admin.more.panel.view_drivers.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingDriverDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _editState = MutableSharedFlow<Resource<Driver>>()
    val editState = _editState.asSharedFlow()

    fun updateStatus(driver: Driver) {
        viewModelScope.launch {
            _editState.emit(Resource.Loading())
        }

        firestore.collection(DRIVER_COLLECTION)
            .whereEqualTo("id", driver.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    document.reference
                        .set(driver)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _editState.emit(Resource.Success(driver))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _editState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _editState.emit(Resource.Error("Driver with specified id not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _editState.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}