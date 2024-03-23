package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.drdisagree.uniride.data.events.AccountStatus
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class Driver(
    val name: String,
    val phone: String,
    val email: String,
    val profileImage: String = "",
    val drivingLicenseFrontImage: String = "",
    val drivingLicenseBackImage: String = "",
    val nidCardFrontImage: String = "",
    val nidCardBackImage: String = "",
    val accountStatus: AccountStatus = AccountStatus.PENDING
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        AccountStatus.PENDING
    )
}