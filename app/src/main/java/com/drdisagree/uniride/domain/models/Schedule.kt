package com.drdisagree.uniride.domain.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
class Schedule(
    val id: Int? = null,
    val busName: String,
    val departureFrom: String,
    val departureFor: String,
    val departureTime: String,
    val busCategory: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        null,
        "",
        "",
        "",
        "",
        "",
        System.currentTimeMillis()
    )
}