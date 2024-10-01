package com.drdisagree.uniride.utils.viewmodels

import androidx.lifecycle.ViewModel
import com.drdisagree.uniride.data.utils.Constant.ADMIN_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountStatusViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin = _isAdmin.asStateFlow()

    private var adminStatusListener: ListenerRegistration? = null

    init {
        fetchUserAdminStatus(firebaseAuth.currentUser?.uid)
    }

    private fun fetchUserAdminStatus(userId: String?) {
        if (userId == null) return

        adminStatusListener?.remove()

        adminStatusListener = firestore.collection(ADMIN_COLLECTION)
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _isAdmin.value = false
                    return@addSnapshotListener
                }

                _isAdmin.value = snapshot?.exists()
            }
    }

    override fun onCleared() {
        super.onCleared()
        adminStatusListener?.remove()
    }
}