package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.drdisagree.uniride.data.events.BusStatus
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Bus(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val time: String,
    val from: String? = null,
    val to: String? = null,
    val departedFrom: String? = null,
    val departedTo: String? = null,
    val departedAt: Long? = null,
    val driver: Driver? = null,
    val currentStatus: BusStatus? = null
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        null,
        null
    )
}