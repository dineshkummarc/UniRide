package com.drdisagree.uniride.data.entities

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
@Entity(tableName = SCHEDULE_COLLECTION)
class ScheduleEntity(
    @PrimaryKey val id: Int? = null,
    val busName: String,
    val departureFrom: String,
    val departureFor: String,
    val departureTime: String,
    val busCategory: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    constructor() : this(
        null,
        "",
        "",
        "",
        "",
        "",
        System.currentTimeMillis()
    )
}