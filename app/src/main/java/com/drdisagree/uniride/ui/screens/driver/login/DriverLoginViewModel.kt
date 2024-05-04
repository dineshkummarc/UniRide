package com.drdisagree.uniride.ui.screens.driver.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.ui.screens.driver.login.utils.LoginValidation
import com.drdisagree.uniride.ui.screens.driver.login.utils.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class DriverLoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val tag = DriverLoginViewModel::class.java.simpleName

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _authenticated = MutableSharedFlow<Resource<String>>()
    val authenticated = _authenticated.asSharedFlow()

    fun login(email: String, password: String) {
        if (checkValidation(email, password)) {
            viewModelScope.launch {
                _login.emit(Resource.Loading())
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    firebaseAuth.currentUser?.isEmailVerified?.let { isEmailVerified ->
                        if (isEmailVerified) {
                            viewModelScope.launch {
                                it.user?.let {
                                    _login.emit(Resource.Success(it))
                                }
                            }
                        } else {
                            viewModelScope.launch {
                                _login.emit(Resource.Error("Please verify your email"))
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))
                    }
                }
        } else {
            viewModelScope.launch {
                var errorSent = false
                if (email.isEmpty()) {
                    _login.emit(Resource.Error("Email cannot be empty"))
                    errorSent = true
                } else if (validateEmail(email) !is LoginValidation.Valid) {
                    _login.emit(Resource.Error("Invalid email"))
                    errorSent = true
                }
                if (password.isEmpty()) {
                    _login.emit(Resource.Error("Password cannot be empty"))
                    errorSent = true
                } else if (password.length < 8) {
                    _login.emit(Resource.Error("Password must be at least 8 characters long"))
                    errorSent = true
                }
                if (!errorSent) {
                    _login.emit(Resource.Error("Invalid email or password"))
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                firebaseAuth.signOut()
            } catch (exception: Exception) {
                if (exception !is CancellationException) {
                    Log.e(tag, "signOut:", exception)
                } else {
                    throw exception
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success("Password reset email sent"))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = password.isNotEmpty() && password.length >= 8
        return emailValidation is LoginValidation.Valid && passwordValidation
    }

    fun resendVerificationMail() {
        viewModelScope.launch {
            _authenticated.emit(Resource.Loading())
        }

        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                viewModelScope.launch {
                    _authenticated.emit(Resource.Success("Verification email sent"))
                }
            }
            ?.addOnFailureListener {
                viewModelScope.launch {
                    _authenticated.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}