package com.drdisagree.uniride.ui.screens.student.schedule

import android.content.Context
import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.BusCategory
import com.drdisagree.uniride.data.models.Place
import com.drdisagree.uniride.data.models.Schedule
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allSchedules = MutableStateFlow<Resource<List<Schedule>>>(Resource.Unspecified())
    val allSchedules = _allSchedules.asStateFlow()

    private val _filteredSchedules = MutableStateFlow<Resource<List<Schedule>>>(Resource.Unspecified())
    val filteredSchedules = _filteredSchedules.asStateFlow()

    init {
        getAllSchedules()
    }

    private fun getAllSchedules() {
        viewModelScope.launch {
            _allSchedules.emit(Resource.Loading())
        }

        firestore.collection(SCHEDULE_COLLECTION)
            .addSnapshotListener { value, error ->
                viewModelScope.launch {
                    if (error != null) {
                        _allSchedules.emit(
                            Resource.Error(
                                error.message.toString()
                            )
                        )
                    } else {
                        value?.let {
                            val schedules = withContext(Dispatchers.IO) {
                                it.toObjects(Schedule::class.java)
                            }
                            val is24HourFormat = DateFormat.is24HourFormat(context)
                            val sortedSchedules = withContext(Dispatchers.Default) {
                                sortSchedulesByTime(schedules, is24HourFormat)
                            }

                            _allSchedules.emit(
                                Resource.Success(
                                    sortedSchedules
                                )
                            )
                        }
                    }
                }
            }
    }

    fun filterSchedules(
        selectedBusCategory: BusCategory?,
        selectedLocationFrom: Place?,
        selectedLocationTo: Place?
    ) {
        viewModelScope.launch {
            _filteredSchedules.emit(Resource.Loading())

            val schedules = (allSchedules.value as? Resource.Success)?.data.orEmpty()
            val filteredList = withContext(Dispatchers.Default) {
                schedules.filter { schedule ->
                    (selectedBusCategory == null || schedule.category == selectedBusCategory) &&
                            (selectedLocationFrom == null || schedule.from == selectedLocationFrom) &&
                            (selectedLocationTo == null || schedule.to == selectedLocationTo)
                }
            }

            _filteredSchedules.emit(Resource.Success(filteredList))
        }
    }
}