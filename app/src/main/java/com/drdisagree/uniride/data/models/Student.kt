package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Student(
    val userId: String,
    val userName: String?,
    val email: String?,
    val profilePictureUrl: String?
) : Parcelable {
    constructor() : this(
        userId = UUID.randomUUID().toString(),
        userName = "",
        email = "",
        profilePictureUrl = ""
    )
}