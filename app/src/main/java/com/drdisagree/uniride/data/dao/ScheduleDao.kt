package com.drdisagree.uniride.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drdisagree.uniride.data.entities.ScheduleEntity
import com.drdisagree.uniride.data.utils.Constant
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM ${Constant.SCHEDULE_COLLECTION}")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query(
        "SELECT * FROM ${Constant.SCHEDULE_COLLECTION} WHERE (:busCategory = '' OR busCategory = :busCategory) " +
                "AND (:departureFrom = '' OR departureFrom = :departureFrom) " +
                "AND (:departureFor = '' OR departureFor = :departureFor)"
    )
    fun searchSchedule(
        busCategory: String,
        departureFrom: String,
        departureFor: String
    ): Flow<List<ScheduleEntity>>
}