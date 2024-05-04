package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Notice(
    val uuid: String = UUID.randomUUID().toString(),
    val announcement: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        announcement = ""
    )
}