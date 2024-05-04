package com.drdisagree.uniride.ui.screens.admin.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.utils.Constant.ADMIN_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AccountStatusViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isAdmin = MutableLiveData<Boolean?>(null)
    val isAdmin: LiveData<Boolean?> get() = _isAdmin

    init {
        firebaseAuth.currentUser?.uid?.let { fetchUserAdminStatus(it) }
    }

    private fun fetchUserAdminStatus(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isAdmin = isUserAdmin(userId)
                _isAdmin.postValue(isAdmin)
            } catch (ignored: Exception) {
                _isAdmin.postValue(false)
            }
        }
    }

    private suspend fun isUserAdmin(userId: String): Boolean {
        return firestore.collection(ADMIN_COLLECTION)
            .document(userId)
            .get()
            .await()
            .exists()
    }
}