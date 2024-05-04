package com.drdisagree.uniride.ui.screens.student.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Notice
import com.drdisagree.uniride.data.utils.Constant.ANNOUNCEMENT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeBoardViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _noticeBoard = MutableStateFlow<Resource<List<Notice>>>(Resource.Unspecified())
    val noticeBoard = _noticeBoard.asStateFlow()

    init {
        getLastAnnouncement()
    }

    private fun getLastAnnouncement() {
        viewModelScope.launch {
            _noticeBoard.emit(Resource.Loading())
        }

        val query = firestore.collection(ANNOUNCEMENT_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    viewModelScope.launch {
                        _noticeBoard.emit(
                            Resource.Success(
                                querySnapshot.toObjects(Notice::class.java)
                            )
                        )
                    }
                } else {
                    viewModelScope.launch {
                        _noticeBoard.emit(
                            Resource.Error("No announcement found")
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _noticeBoard.emit(Resource.Error(exception.message.toString()))
                }
            }

        query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                viewModelScope.launch {
                    _noticeBoard.emit(Resource.Error(error.message.toString()))
                }
            } else {
                snapshot?.let {
                    if (!it.isEmpty) {
                        viewModelScope.launch {
                            _noticeBoard.emit(
                                Resource.Success(
                                    it.toObjects(Notice::class.java)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}