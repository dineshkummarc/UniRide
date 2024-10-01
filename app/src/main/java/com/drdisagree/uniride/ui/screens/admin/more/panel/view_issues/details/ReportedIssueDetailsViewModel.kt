package com.drdisagree.uniride.ui.screens.admin.more.panel.view_issues.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Issue
import com.drdisagree.uniride.data.utils.Constant.ISSUE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportedIssueDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _editState = MutableSharedFlow<Resource<Issue>>()
    val editState = _editState.asSharedFlow()

    private val _deleteState = MutableSharedFlow<Resource<String>>()
    val deleteState = _deleteState.asSharedFlow()

    fun editIssue(issue: Issue) {
        viewModelScope.launch {
            _editState.emit(Resource.Loading())
        }

        firestore.collection(ISSUE_COLLECTION)
            .whereEqualTo("uuid", issue.uuid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    document.reference
                        .set(issue)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _editState.emit(Resource.Success(issue))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _editState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _editState.emit(Resource.Error("Issue with specified id not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _editState.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun deleteIssue(uuid: String) {
        viewModelScope.launch {
            _deleteState.emit(Resource.Loading())
        }

        firestore.collection(ISSUE_COLLECTION)
            .whereEqualTo("uuid", uuid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]

                    document.reference
                        .delete()
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _deleteState.emit(Resource.Success("Issue deleted successfully"))
                            }
                        }
                        .addOnFailureListener { exception ->
                            viewModelScope.launch {
                                _deleteState.emit(Resource.Error(exception.message.toString()))
                            }
                        }
                } else {
                    viewModelScope.launch {
                        _deleteState.emit(Resource.Error("Issue with specified id not found"))
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    _editState.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
}