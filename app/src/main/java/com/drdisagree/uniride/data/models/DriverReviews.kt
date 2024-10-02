package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class DriverReviews(
    val id: String = UUID.randomUUID().toString(),
    val about: Driver,
    val summarization: String? = null,
    val reviews: List<Review>
) : Parcelable {
    constructor() : this(
        about = Driver(),
        reviews = emptyList()
    )
}