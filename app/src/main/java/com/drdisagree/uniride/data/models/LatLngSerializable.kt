package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class LatLngSerializable(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)

    companion object {
        fun fromLatLng(latLng: LatLng): LatLngSerializable {
            return LatLngSerializable(latLng.latitude, latLng.longitude)
        }
    }
}