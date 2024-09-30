package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Issue(
    val uuid: String = UUID.randomUUID().toString(),
    val type: String,
    val description: String,
    val contactInfo: String,
    val isResolved: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        type = "",
        description = "",
        contactInfo = ""
    )
}