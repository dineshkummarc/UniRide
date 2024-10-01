package com.drdisagree.uniride.ui.screens.admin.more.panel.view_drivers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.AccountStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PendingDriversViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _drivers = MutableStateFlow<Resource<List<Driver>>>(Resource.Unspecified())
    val drivers = _drivers.asStateFlow()

    init {
        getPendingDrivers()
    }

    private fun getPendingDrivers() {
        viewModelScope.launch {
            _drivers.emit(Resource.Loading())
        }

        val sortDrivers = { drivers: List<Driver> ->
            drivers.sortedWith(
                compareBy<Driver> { driver ->
                    when (driver.accountStatus) {
                        AccountStatus.PENDING -> 0
                        AccountStatus.APPROVED -> 1
                        AccountStatus.REJECTED -> 2
                    }
                }.thenByDescending { driver -> driver.timeStamp }
            )
        }

        firestore.collection(DRIVER_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _drivers.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    value?.let {
                        val drivers = it.toObjects(Driver::class.java)
                        viewModelScope.launch {
                            _drivers.emit(Resource.Success(sortDrivers(drivers)))
                        }
                    }
                }
            }
    }
}