package com.drdisagree.uniride.ui.screens.student.account

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.data.utils.Constant
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
@Suppress("deprecation")
class StudentSignInViewModel @Inject constructor(
    private val oneTapClient: SignInClient,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val tag = StudentSignInViewModel::class.java.simpleName

    private val _loadingState = MutableSharedFlow<Resource<String>>()
    val loadingState = _loadingState.asSharedFlow()

    private val _loginState = MutableStateFlow(SignInState())
    val loginState = _loginState.asStateFlow()

    fun signInWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch {
            val signInIntentSender = signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                oneTapClient.signOut().await()
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

    fun getSignedInStudent(): Student {
        val currentUser = firebaseAuth.currentUser!!

        return Student(
            userId = currentUser.uid,
            userName = currentUser.displayName,
            email = currentUser.email,
            profilePictureUrl = currentUser.photoUrl?.toString()
        )
    }

    fun handleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            viewModelScope.launch {
                _loadingState.emit(Resource.Loading())

                val signInResult = signInWithIntent(result.data ?: return@launch)
                onSignInResult(signInResult)
            }
        }
    }

    fun resetState() {
        _loginState.update { SignInState() }
    }

    private fun onSignInResult(result: SignInResult) {
        val isSuccessful = result.data != null

        _loginState.update {
            it.copy(
                isSuccessful = isSuccessful,
                signInError = result.errorMessage
            )
        }

        viewModelScope.launch {
            _loadingState.emit(
                if (isSuccessful) {
                    Resource.Unspecified()
                } else {
                    Resource.Error("Login failed")
                }
            )
        }
    }

    private suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (exception: Exception) {
            if (exception !is CancellationException) {
                Log.e(tag, "signIn:", exception)
            } else {
                throw exception
            }
            null
        }

        return result?.pendingIntent?.intentSender
    }

    private suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(
            googleIdToken,
            null
        )

        if (!Constant.STUDENT_MAIL_SUFFIX.any { suffix -> credential.id.endsWith(suffix) }) {
            return SignInResult(
                data = null,
                errorMessage = "You are not a student of DIU"
            )
        }

        return try {
            val user = firebaseAuth.signInWithCredential(googleCredentials).await().user!!

            val student = Student(
                userId = user.uid,
                userName = user.displayName,
                email = user.email,
                profilePictureUrl = user.photoUrl?.toString()
            )
            val studentCollection = firestore.collection(Constant.STUDENT_COLLECTION)
            studentCollection.document(user.uid)
                .set(student)
                .await()

            SignInResult(
                data = student,
                errorMessage = null
            )
        } catch (exception: Exception) {
            if (exception !is CancellationException) {
                Log.e(tag, "signInWithIntent:", exception)
            } else {
                throw exception
            }

            SignInResult(
                null,
                exception.message
            )
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(Constant.WEB_CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}