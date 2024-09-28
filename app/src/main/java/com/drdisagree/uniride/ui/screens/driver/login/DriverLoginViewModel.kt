package com.drdisagree.uniride.ui.screens.driver.login

import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.utils.Constant.DRIVER_DOCUMENT_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.PHONE_NUMBER_PREFIX
import com.drdisagree.uniride.ui.screens.driver.login.utils.LoginValidation
import com.drdisagree.uniride.ui.screens.driver.login.utils.validateEmail
import com.drdisagree.uniride.ui.screens.driver.login.utils.validatePhoneNumber
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DriverLoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storage: StorageReference
) : ViewModel() {

    private val tag = DriverLoginViewModel::class.java.simpleName

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _verifyEmail = MutableSharedFlow<Resource<String>>()
    val verifyEmail = _verifyEmail.asSharedFlow()

    fun loginWithEmailPassword(email: String, password: String) {
        if (isEmailPasswordValid(email, password)) {
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
                                _login.emit(Resource.Error("EmailNotVerified"))
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

    fun loginWithPhoneNumber(
        phone: String,
        activity: Activity,
        onCodeSent: (String, PhoneAuthProvider.ForceResendingToken) -> Unit,
    ) {
        if (isPhoneNumberValid(phone)) {
            viewModelScope.launch {
                _login.emit(Resource.Loading())
            }

            val phoneNumber = if (!phone.startsWith(PHONE_NUMBER_PREFIX)) {
                "$PHONE_NUMBER_PREFIX${phone.drop(1)}"
            } else {
                phone
            }

            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    verifyPhoneNumberWithCode(phoneAuthCredential = phoneAuthCredential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("createUserWithPhoneNumber", "onVerificationFailed: $e")

                    viewModelScope.launch {
                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                _login.emit(Resource.Error("Invalid OTP provided"))
                            }

                            is FirebaseTooManyRequestsException -> {
                                _login.emit(Resource.Error("Too many requests, please try again later"))
                            }

                            is FirebaseAuthMissingActivityForRecaptchaException -> {
                                _login.emit(Resource.Error("reCAPTCHA verification failed"))
                            }

                            else -> _login.emit(Resource.Error(e.message.toString()))
                        }
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d("createUserWithPhoneNumber", "onCodeSent: $verificationId")

                    onCodeSent(verificationId, token)
                }
            }

            val phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
        } else {
            viewModelScope.launch {
                var errorSent = false
                if (phone.isEmpty()) {
                    _login.emit(Resource.Error("Phone number cannot be empty"))
                    errorSent = true
                } else if (phone.length != 11 && phone.length != 14) {
                    _login.emit(Resource.Error("Invalid phone number"))
                    errorSent = true
                } else if (!Patterns.PHONE.matcher(phone).matches()) {
                    _login.emit(Resource.Error("Invalid phone number format"))
                    errorSent = true
                }
                if (!errorSent) {
                    _login.emit(Resource.Error("Phone number is invalid"))
                }
            }
        }
    }

    fun verifyPhoneNumberWithCode(phoneAuthCredential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
            .addOnSuccessListener { authResult ->
                authResult.user?.let { user ->
                    val userStorageRef = storage.child(DRIVER_DOCUMENT_COLLECTION).child(user.uid)

                    userStorageRef.listAll()
                        .addOnSuccessListener { listResult ->
                            if (listResult.items.isNotEmpty()) {
                                viewModelScope.launch {
                                    _login.emit(Resource.Success(user))
                                }
                            } else {
                                user.delete()
                                viewModelScope.launch {
                                    _login.emit(Resource.Error("Account not found"))
                                }
                            }
                        }.addOnFailureListener { e ->
                            Log.e(
                                "verifyPhoneNumberWithCode",
                                "Error checking account existence: $e"
                            )
                            viewModelScope.launch {
                                _login.emit(Resource.Error(e.message.toString()))
                            }
                        }
                }
            }
            .addOnFailureListener {
                Log.e(
                    "verifyPhoneNumberWithCode",
                    "addOnFailureListener: ${it.message}"
                )
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
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

    private fun isEmailPasswordValid(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = password.isNotEmpty() && password.length >= 8
        return emailValidation is LoginValidation.Valid && passwordValidation
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        return validatePhoneNumber(phone) is LoginValidation.Valid
    }

    fun resendVerificationMail() {
        viewModelScope.launch {
            _verifyEmail.emit(Resource.Loading())
        }

        firebaseAuth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                viewModelScope.launch {
                    _verifyEmail.emit(Resource.Success("Verification email sent"))
                }
            }
            ?.addOnFailureListener {
                viewModelScope.launch {
                    _verifyEmail.emit(Resource.Error(it.message.toString()))
                }
            }
            ?: viewModelScope.launch {
                _verifyEmail.emit(Resource.Error("User not found"))
            }
    }
}