package com.drdisagree.uniride.utils.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetDriverViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _getDriver = MutableStateFlow<Resource<Driver>>(Resource.Loading())
    val getDriver: StateFlow<Resource<Driver>> = _getDriver

    init {
        firebaseAuth.currentUser?.let {
            getSignedInDriver()
        }
    }

    private fun getSignedInDriver() {
        firestore.collection(Constant.DRIVER_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    document.toObject(Driver::class.java)?.let { driver ->
                        viewModelScope.launch {
                            _getDriver.emit(Resource.Success(driver))
                        }
                    }
                } else {
                    viewModelScope.launch {
                        _getDriver.emit(Resource.Error("Account information not found"))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _getDriver.emit(
                        Resource.Error(it.message.toString())
                    )
                }
            }
    }
}