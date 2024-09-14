package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Stable
@Parcelize
data class Route(
    val uuid: String = UUID.randomUUID().toString(),
    val routeNo: String,
    val routeCategory: RouteCategory,
    val routeName: String,
    val routeDetails: String,
    val startTime: String,
    val departureTime: String,
    val routeWebUrl: String?,
    val timeStamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        routeNo = "",
        routeCategory = RouteCategory(),
        routeName = "",
        routeDetails = "",
        startTime = "",
        departureTime = "",
        routeWebUrl = ""
    )
}