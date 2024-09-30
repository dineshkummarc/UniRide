package com.drdisagree.uniride.ui.screens.admin.more.panel.features.view_issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Issue
import com.drdisagree.uniride.data.utils.Constant.ISSUE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportedIssuesViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _issues = MutableStateFlow<Resource<List<Issue>>>(Resource.Unspecified())
    val issues = _issues.asStateFlow()

    init {
        getReportedIssues()
    }

    private fun getReportedIssues() {
        viewModelScope.launch {
            _issues.emit(Resource.Loading())
        }

        val sortDrivers = { issues: List<Issue> ->
            issues.sortedWith(
                compareBy<Issue> { issue ->
                    when (issue.isResolved) {
                        false -> 0
                        true -> 1
                    }
                }.thenByDescending { issue -> issue.timeStamp }
            )
        }

        firestore.collection(ISSUE_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    val issues = result.toObjects(Issue::class.java)
                    _issues.emit(Resource.Success(sortDrivers(issues)))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _issues.emit(Resource.Error(it.message.toString()))
                }
            }

        firestore.collection(ISSUE_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch {
                        _issues.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    value?.let {
                        val issues = it.toObjects(Issue::class.java)
                        viewModelScope.launch {
                            _issues.emit(Resource.Success(sortDrivers(issues)))
                        }
                    }
                }
            }
    }
}