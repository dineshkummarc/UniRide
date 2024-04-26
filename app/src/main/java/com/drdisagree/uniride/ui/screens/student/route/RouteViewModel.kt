package com.drdisagree.uniride.ui.screens.student.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Route
import com.drdisagree.uniride.data.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allRoutes = MutableStateFlow<Resource<List<Route>>>(Resource.Unspecified())
    val allRoutes = _allRoutes.asStateFlow()

    init {
        getAllRoutes()
    }

    private fun getAllRoutes() {
        viewModelScope.launch {
            _allRoutes.emit(Resource.Loading())
        }

        firestore.collection(Constant.ROUTE_COLLECTION)
            .orderBy("routeNo", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                viewModelScope.launch {
                    _allRoutes.emit(
                        Resource.Success(
                            it.toObjects(
                                Route::class.java
                            )
                        )
                    )
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _allRoutes.emit(
                        Resource.Error(
                            it.message.toString()
                        )
                    )
                }
            }

        firestore.collection(Constant.ROUTE_COLLECTION)
            .orderBy("routeNo", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _allRoutes.emit(
                            Resource.Error(
                                error.message.toString()
                            )
                        )
                    }
                } else {
                    value?.let {
                        viewModelScope.launch {
                            _allRoutes.emit(
                                Resource.Success(
                                    it.toObjects(
                                        Route::class.java
                                    )
                                )
                            )
                        }
                    }
                }
            }
    }
}