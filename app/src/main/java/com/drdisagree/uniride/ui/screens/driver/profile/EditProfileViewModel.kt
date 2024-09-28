package com.drdisagree.uniride.ui.screens.driver.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drdisagree.uniride.data.events.Resource
import com.drdisagree.uniride.data.models.Driver
import com.drdisagree.uniride.data.utils.Constant.DRIVER_COLLECTION
import com.drdisagree.uniride.data.utils.Constant.DRIVER_DOCUMENT_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val application: Application,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: StorageReference
) : ViewModel() {

    private val _update = MutableStateFlow<Resource<Driver>>(Resource.Unspecified())
    val update: Flow<Resource<Driver>> = _update

    fun updateProfile(
        driver: Driver,
        image: Pair<Uri?, String>
    ) {
        viewModelScope.launch {
            _update.emit(Resource.Loading())

            val userStorageRef = storage.child(DRIVER_DOCUMENT_COLLECTION).child(driver.id)

            try {
                var updatedDriver: Driver = driver

                if (image.first != null) {
                    val (byteArray, documentName) = getImageByteArray(image.first!! to image.second)

                    val imageUrl = withContext(Dispatchers.IO) {
                        val imageStorageRef = userStorageRef.child(documentName)
                        val uploadTask = imageStorageRef.putBytes(byteArray)
                        val result = uploadTask.await()

                        result.storage.downloadUrl.await().toString()
                    }

                    updatedDriver = driver.copy(
                        profileImage = imageUrl
                    )
                }

                firestore.runTransaction { transaction ->
                    val documentRef = firestore.collection(DRIVER_COLLECTION).document(driver.id)

                    transaction.set(documentRef, updatedDriver)
                }.addOnSuccessListener {
                    _update.value = Resource.Success(updatedDriver)
                }.addOnFailureListener {
                    _update.value = Resource.Error(it.message.toString())
                }
            } catch (e: Exception) {
                _update.value = Resource.Error(e.message.toString())
            }
        }
    }

    private suspend fun getImageByteArray(pair: Pair<Uri, String>): Pair<ByteArray, String> =
        withContext(Dispatchers.IO) {
            val stream = ByteArrayOutputStream()
            val imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("deprecation") MediaStore.Images.Media.getBitmap(
                    application.contentResolver,
                    pair.first
                )
            } else {
                val source: ImageDecoder.Source = ImageDecoder.createSource(
                    application.contentResolver,
                    pair.first
                )
                ImageDecoder.decodeBitmap(source)
            }
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            return@withContext Pair(stream.toByteArray(), pair.second)
        }
}