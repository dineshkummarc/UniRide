package com.drdisagree.uniride.ui.screens.driver.register

import androidx.lifecycle.ViewModel
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant
import com.drdisagree.uniride.ui.screens.driver.register.utils.RegisterFieldState
import com.drdisagree.uniride.ui.screens.driver.register.utils.RegisterValidation
import com.drdisagree.uniride.ui.screens.driver.register.utils.validateEmail
import com.drdisagree.uniride.ui.screens.driver.register.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<Driver>>(Resource.Unspecified())
    val register: Flow<Resource<Driver>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createUserWithEmailAndPassword(driver: Driver, password: String) {
        if (checkValidation(driver, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(driver.email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            authResult.user?.let {
                                saveUserInfo(
                                    it.uid,
                                    driver
                                )
                            }
                        }
                        ?.addOnFailureListener {
                            _register.value = Resource.Error(it.message.toString())
                        }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            runBlocking {
                _validation.send(
                    RegisterFieldState(
                        validateEmail(driver.email),
                        validatePassword(password)
                    )
                )
            }
        }
    }

    private fun checkValidation(driver: Driver, password: String): Boolean {
        val emailValidation = validateEmail(driver.email)
        val passwordValidation = validatePassword(password)
        return emailValidation is RegisterValidation.Valid && passwordValidation is RegisterValidation.Valid
    }

    private fun saveUserInfo(userUid: String, user: Driver) {
        firestore.collection(Constant.DRIVER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }
}