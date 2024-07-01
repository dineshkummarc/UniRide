package com.drdisagree.uniride.ui.screens.admin.more.panel.features.new_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.data.utils.Constant.PLACE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewLocationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<String>>()
    val state = _state.asSharedFlow()

    fun saveLocation(place: Place) {
        viewModelScope.launch {
            _state.emit(Resource.Loading())
        }

        val uuid = UUID.randomUUID().toString()

        firestore.collection(PLACE_COLLECTION)
            .document(uuid)
            .set(
                place.copy(
                    uuid = uuid
                )
            )
            .addOnSuccessListener {
                viewModelScope.launch {
                    _state.emit(Resource.Success("Location saved successfully"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}