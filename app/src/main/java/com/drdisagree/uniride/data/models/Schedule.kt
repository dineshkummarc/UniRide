package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Schedule(
    val uuid: String = UUID.randomUUID().toString(),
    val bus: Bus,
    val category: BusCategory,
    val from: String,
    val to: String,
    val time: String,
) : Parcelable {
    constructor() : this(
        "",
        Bus(
            "",
            ""
        ),
        BusCategory(
            "",
            ""
        ),
        "",
        "",
        ""
    )
}