package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Review(
    val uuid: String = UUID.randomUUID().toString(),
    val submittedBy: Student,
    val message: String,
    val rating: Int,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        submittedBy = Student(),
        message = "",
        rating = 0
    )
}