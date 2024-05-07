package com.drdisagree.uniride.ui.screens.admin.route.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.data.utils.Constant.ROUTE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditRouteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _editState = MutableSharedFlow<Resource<Route>>()
    val editState = _editState.asSharedFlow()

    private val _deleteState = MutableSharedFlow<Resource<String>>()
    val deleteState = _deleteState.asSharedFlow()

    fun editRoute(route: Route) {
        viewModelScope.launch {
            _editState.emit(Resource.Loading())
        }

        firestore.collection(ROUTE_COLLECTION)
            .whereEqualTo("uuid", route.uuid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    document.reference
                        .set(route)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _editState.emit(Resource.Success(route))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _editState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _editState.emit(Resource.Error("Route with specified id not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _editState.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun deleteRoute(uuid: String) {
        viewModelScope.launch {
            _deleteState.emit(Resource.Loading())
        }

        firestore.collection(ROUTE_COLLECTION)
            .whereEqualTo("uuid", uuid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    document.reference
                        .delete()
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _deleteState.emit(Resource.Success("Route deleted successfully"))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _deleteState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _deleteState.emit(Resource.Error("Route with specified id not found"))
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