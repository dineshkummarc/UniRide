package com.drdisagree.uniride.ui.screens.student.account

import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.ViewModel
import com.drdisagree.uniride.data.models.Student
import com.drdisagree.uniride.data.utils.Constant.STUDENT_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.STUDENT_MAIL_SUFFIX
import com.drdisagree.uniride.data.utils.Constant.WEB_CLIENT_ID
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
@Suppress("deprecation")
class GoogleAuthUiClient @Inject constructor(
    private val oneTapClient: SignInClient,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val tag = GoogleAuthUiClient::class.java.simpleName

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

        if (!STUDENT_MAIL_SUFFIX.any { suffix -> credential.id.endsWith(suffix) }) {
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
            val studentCollection = firestore.collection(STUDENT_COLLECTION)
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

    fun getSignedInUser(): Student {
        val currentUser = firebaseAuth.currentUser!!

        return Student(
            userId = currentUser.uid,
            userName = currentUser.displayName,
            email = currentUser.email,
            profilePictureUrl = currentUser.photoUrl?.toString()
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