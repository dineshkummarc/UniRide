package com.drdisagree.uniride.ui.screens.admin.more.panel.features.new_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Bus
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.data.utils.Constant.BUS_CATEGORY_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.BUS_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewScheduleViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<String>>()
    val state = _state.asSharedFlow()

    private val _busModels = MutableStateFlow<List<Bus>>(emptyList())
    val busModels: StateFlow<List<Bus>> = _busModels

    private val _busCategoryModels = MutableStateFlow<List<BusCategory>>(emptyList())
    val busCategoryModels: StateFlow<List<BusCategory>> = _busCategoryModels

    init {
        getBusList()
        getBusCategoryList()
    }

    fun saveSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _state.emit(Resource.Loading())
        }

        val uuid = UUID.randomUUID().toString()

        firestore.collection(SCHEDULE_COLLECTION)
            .document(uuid)
            .set(
                schedule.copy(
                    uuid = uuid
                )
            )
            .addOnSuccessListener {
                viewModelScope.launch {
                    _state.emit(Resource.Success("Schedule saved successfully"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun getBusList() {
        firestore.collection(BUS_COLLECTION)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val models = querySnapshot.documents.mapNotNull { doc ->
                    Bus(
                        uuid = doc.id,
                        name = doc.getString("name") ?: "",
                    )
                }.sortedWith(compareBy { it.name })

                _busModels.value = models
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
                    BusCategory(
                        uuid = doc.id,
                        name = doc.getString("name") ?: "",
                    )
                }.sortedWith(compareBy { it.name })

                _busCategoryModels.value = models
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _state.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}