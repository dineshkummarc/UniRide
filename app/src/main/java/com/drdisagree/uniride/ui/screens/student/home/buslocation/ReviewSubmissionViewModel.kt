package com.drdisagree.uniride.ui.screens.student.home.buslocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.models.DriverReviews
import com.drdisagree.uniride.data.models.Review
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.data.utils.Constant.DRIVER_REVIEW_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.REVIEW_SUMMARY_PROMPT
import com.drdisagree.uniride.di.GenerativeModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReviewSubmissionViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableSharedFlow<Resource<Unit>>()
    val state = _state.asSharedFlow()

    private val _summary = MutableStateFlow<Resource<String>>(Resource.Loading())
    val summary: StateFlow<Resource<String>> = _summary.asStateFlow()

    fun submitReview(student: Student, driver: Driver, reviewMessage: String, rating: Int) {
        viewModelScope.launch {
            _state.emit(
                Resource.Loading()
            )

            try {
                firestore.runTransaction { transaction ->
                    val driverReviewsRef = firestore
                        .collection(DRIVER_REVIEW_COLLECTION)
                        .document(driver.id)

                    val driverReviewsSnapshot = transaction.get(driverReviewsRef)
                    val driverReviews = driverReviewsSnapshot.toObject(DriverReviews::class.java)
                        ?: DriverReviews(
                            id = driver.id,
                            about = driver,
                            reviews = emptyList()
                        )

                    if (driverReviews.reviews.any { it.submittedBy.userId == student.userId }) {
                        viewModelScope.launch {
                            _state.emit(
                                Resource.Error(
                                    "You have already submitted a review for this driver"
                                )
                            )
                        }
                        return@runTransaction
                    }

                    val newReview = Review(
                        submittedBy = student,
                        message = reviewMessage,
                        rating = rating
                    )

                    val updatedReviews = driverReviews.reviews.toMutableList().apply {
                        add(newReview)
                        sortByDescending { it.timeStamp }
                    }

                    val prompt = REVIEW_SUMMARY_PROMPT +
                            updatedReviews.take(50).joinToString("\n\n") { it.message }

                    val response = runBlocking(Dispatchers.IO) {
                        GenerativeModelProvider.generativeModel.generateContent(prompt)
                    }

                    val updatedDriverReviews = driverReviews.copy(
                        summarization = response.text,
                        reviews = updatedReviews
                    )

                    transaction.set(driverReviewsRef, updatedDriverReviews)
                }.addOnSuccessListener {
                    viewModelScope.launch {
                        _state.emit(
                            Resource.Success(Unit)
                        )
                    }
                }.addOnFailureListener { exception ->
                    viewModelScope.launch {
                        _state.emit(
                            Resource.Error(exception.message.toString())
                        )
                    }
                }
            } catch (exception: Exception) {
                _state.emit(
                    Resource.Error(exception.message.toString())
                )
            }
        }
    }

    fun fetchSummary(driverId: String?) {
        viewModelScope.launch {
            if (driverId == null) {
                _summary.emit(
                    Resource.Error("No reviews yet")
                )
                return@launch
            } else {
                _summary.emit(
                    Resource.Loading()
                )
            }

            try {
                val driverReviewsRef =
                    firestore.collection(DRIVER_REVIEW_COLLECTION).document(driverId)
                val driverReviewsSnapshot = driverReviewsRef.get().await()
                val driverReviews = driverReviewsSnapshot.toObject(DriverReviews::class.java)

                if (driverReviews != null) {
                    _summary.emit(
                        Resource.Success(driverReviews.summarization ?: "No reviews yet")
                    )
                } else {
                    _summary.emit(
                        Resource.Error("No reviews yet")
                    )
                }
            } catch (exception: Exception) {
                _summary.emit(
                    Resource.Error(exception.message.toString())
                )
            }
        }
    }
}