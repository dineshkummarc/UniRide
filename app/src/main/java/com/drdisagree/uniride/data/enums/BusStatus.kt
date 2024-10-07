package com.drdisagree.uniride.data.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class BusStatus : Parcelable {
    STANDBY,
    RUNNING,
    STOPPED
}