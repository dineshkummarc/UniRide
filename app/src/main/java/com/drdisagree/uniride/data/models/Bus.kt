package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.drdisagree.uniride.data.events.BusStatus
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Bus(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val category: BusCategory? = null,
    val driver: Driver? = null,
    val status: BusStatus? = null,
    val departedFrom: Place? = null,
    val departedTo: Place? = null,
    val departedAt: Long? = null,
    val currentlyAt: LatLng? = null
) : Parcelable {
    constructor() : this(
        "",
        ""
    )
}