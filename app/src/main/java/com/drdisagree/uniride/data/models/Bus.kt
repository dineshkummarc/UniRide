package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Bus(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val category: BusCategory? = null,
    val driver: Driver? = null,
    val departedFrom: String? = null,
    val departedTo: String? = null,
    val departedAt: Long? = null,
) : Parcelable {
    constructor() : this(
        "",
        ""
    )
}