package com.drdisagree.uniride.utils.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.utils.Constant.ADMIN_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AccountStatusViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin = _isAdmin.asStateFlow()

    init {
        fetchUserAdminStatus(firebaseAuth.currentUser?.uid)
    }

    private fun fetchUserAdminStatus(userId: String?) {
        if (userId == null) return

        viewModelScope.launch {
            try {
                val isAdmin = isUserAdmin(userId)
                _isAdmin.emit(isAdmin)
            } catch (ignored: Exception) {
                _isAdmin.emit(false)
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