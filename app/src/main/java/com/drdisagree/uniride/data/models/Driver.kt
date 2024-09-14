package com.drdisagree.uniride.data.models

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.drdisagree.uniride.data.events.AccountStatus
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class Driver(
    val id: String,
    val name: String,
    val phone: String? = null,
    val email: String,
    val profileImage: String? = null,
    val documents: List<String>,
    val accountStatus: AccountStatus = AccountStatus.PENDING
) : Parcelable {
    constructor() : this(
        id = "",
        name = "",
        email = "",
        documents = emptyList(),
    )
}