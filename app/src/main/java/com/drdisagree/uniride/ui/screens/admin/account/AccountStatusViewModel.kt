package com.drdisagree.uniride.ui.screens.admin.account

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
    firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private var isAdmin: Boolean? = null

    init {
        fetchUserAdminStatus(firebaseAuth.currentUser!!.uid)
    }

    private fun fetchUserAdminStatus(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isAdmin = isUserAdmin(userId)
        }
    }

    fun isUserAdmin(): Boolean? {
        return isAdmin
    }

    private suspend fun isUserAdmin(userId: String): Boolean {
        return try {
            firestore.collection(ADMIN_COLLECTION).document(userId).get().await().exists()
        } catch (e: Exception) {
            false
        }
    }
}