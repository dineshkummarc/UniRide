package com.drdisagree.uniride.data.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AccountStatus : Parcelable {
    PENDING,
    APPROVED,
    REJECTED
}