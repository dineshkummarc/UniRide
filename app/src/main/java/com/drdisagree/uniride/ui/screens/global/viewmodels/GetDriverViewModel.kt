package com.drdisagree.uniride.ui.screens.global.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetDriverViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _getDriver = MutableSharedFlow<Resource<Driver>>()
    val getDriver = _getDriver.asSharedFlow()

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
                    _getDriver.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}