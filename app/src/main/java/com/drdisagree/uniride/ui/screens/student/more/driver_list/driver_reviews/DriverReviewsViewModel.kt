package com.drdisagree.uniride.ui.screens.student.more.driver_list.driver_reviews

import androidx.lifecycle.ViewModel
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.DriverReviews
import com.drdisagree.uniride.data.models.Review
import com.drdisagree.uniride.data.utils.Constant.DRIVER_REVIEW_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DriverReviewsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Review>>>(Resource.Unspecified())
    val state = _state.asStateFlow()

    init {
        fetchAllReviews(firebaseAuth.uid.toString())
    }

    private fun fetchAllReviews(driverId: String) {
        _state.value = Resource.Loading()

        try {
            val driverReviewsRef = firestore
                .collection(DRIVER_REVIEW_COLLECTION)
                .document(driverId)

            driverReviewsRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _state.value = Resource.Error(error.message.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val driverReviews = snapshot.toObject(DriverReviews::class.java)
                    if (driverReviews != null) {
                        _state.value = Resource.Success(driverReviews.reviews)
                    } else {
                        _state.value = Resource.Success(emptyList())
                    }
                } else {
                    _state.value = Resource.Success(emptyList())
                }
            }
        } catch (exception: Exception) {
            _state.value = Resource.Error(exception.message.toString())
        }
    }
}