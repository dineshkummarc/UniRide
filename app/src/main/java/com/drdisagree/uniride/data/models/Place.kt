package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Place(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val latlng: LatLngSerializable? = null
) : Parcelable {
    constructor() : this(
        name = ""
    )
}