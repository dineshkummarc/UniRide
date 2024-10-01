package com.drdisagree.uniride.ui.screens.admin.more.panel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.AccountStatus
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.ISSUE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _issueCount = MutableStateFlow<Resource<Int>>(Resource.Unspecified())
    val issueCount = _issueCount.asStateFlow()

    private val _pendingDriverCount = MutableStateFlow<Resource<Int>>(Resource.Unspecified())
    val pendingDriverCount = _pendingDriverCount.asStateFlow()

    init {
        getIssueCount()
        getPendingDriversCount()
    }

    private fun getIssueCount() {
        viewModelScope.launch {
            _issueCount.emit(Resource.Loading())
        }

        firestore.collection(ISSUE_COLLECTION)
            .whereEqualTo("resolved", false)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        Log.e("AdminPanelViewModel", error.message.toString())
                        _issueCount.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val count = querySnapshot?.size() ?: 0
                    viewModelScope.launch {
                        _issueCount.emit(Resource.Success(count))
                    }
                }
            }
    }

    private fun getPendingDriversCount() {
        viewModelScope.launch {
            _pendingDriverCount.emit(Resource.Loading())
        }

        firestore.collection(DRIVER_COLLECTION)
            .whereEqualTo("accountStatus", AccountStatus.PENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    viewModelScope.launch {
                        Log.e("AdminPanelViewModel", error.message.toString())
                        _pendingDriverCount.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    val count = querySnapshot?.size() ?: 0
                    viewModelScope.launch {
                        _pendingDriverCount.emit(Resource.Success(count))
                    }
                }
            }
    }
}