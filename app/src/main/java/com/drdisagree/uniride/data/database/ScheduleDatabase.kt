package com.drdisagree.uniride.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.drdisagree.uniride.data.dao.ScheduleDao
import com.drdisagree.uniride.data.entities.ScheduleEntity

@Database(
    entities = [ScheduleEntity::class],
    version = 1
)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract val dao: ScheduleDao
}