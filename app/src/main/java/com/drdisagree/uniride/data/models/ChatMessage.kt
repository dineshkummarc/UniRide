package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val sender: Student,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        message = "",
        sender = Student()
    )
}