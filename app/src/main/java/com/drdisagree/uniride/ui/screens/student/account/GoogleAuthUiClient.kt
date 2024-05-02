package com.drdisagree.uniride.ui.screens.student.account

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.data.utils.Constant.STUDENT_MAIL_SUFFIX
import com.drdisagree.uniride.data.utils.Constant.WEB_CLIENT_ID
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

@Suppress("deprecation")
class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val tag = GoogleAuthUiClient::class.java.simpleName
    private val firebaseAuth = Firebase.auth

    suspend fun signIn(): IntentSender? {
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

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(
            googleIdToken,
            null
        )

        if (!credential.id.endsWith(STUDENT_MAIL_SUFFIX)) {
            firebaseAuth.currentUser?.delete()
            return SignInResult(
                data = null,
                errorMessage = "You are not a student of DIU"
            )
        }

        return try {
            val user = firebaseAuth.signInWithCredential(googleCredentials).await().user

            return SignInResult(
                data = user?.run {
                        Student(
                            userId = uid,
                            userName = displayName,
                            profilePictureUrl = photoUrl?.toString()
                        )
                    },
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

    suspend fun signOut() {
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

    fun getSignedInUser(): Student? = firebaseAuth.currentUser?.run {
        Student(
            userId = uid,
            userName = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(WEB_CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}