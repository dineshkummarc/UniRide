package com.drdisagree.uniride.ui.screens.student.more.driver_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.models.DriverReviews
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.DRIVER_REVIEW_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class DriverListViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<DriverReviews>>>(Resource.Unspecified())
    val state = _state.asStateFlow()

    init {
        fetchAllDriversWithReviews()
    }

    private fun fetchAllDriversWithReviews() {
        viewModelScope.launch {
            _state.emit(Resource.Loading())

            try {
                val driverSnapshots = firestore.collection(DRIVER_COLLECTION)
                    .whereEqualTo("accountStatus", "APPROVED")
                    .get()
                    .await()

                val drivers = driverSnapshots.toObjects(Driver::class.java)
                val driverList = drivers.map { driver ->
                    async {
                        val reviewsSnapshot = firestore.collection(DRIVER_REVIEW_COLLECTION)
                            .document(driver.id)
                            .get()
                            .await()

                        val reviews = if (reviewsSnapshot.exists()) {
                            reviewsSnapshot.toObject(DriverReviews::class.java)
                        } else {
                            null
                        }

                        DriverReviews(
                            id = reviews?.id ?: "",
                            about = driver,
                            summarization = reviews?.summarization,
                            reviews = reviews?.reviews ?: emptyList()
                        )
                    }
                }.awaitAll()

                _state.emit(Resource.Success(driverList))
            } catch (exception: Exception) {
                _state.emit(Resource.Error(exception.message.toString()))
            }
        }
    }
}