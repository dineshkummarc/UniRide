package com.drdisagree.uniride.domain.repository

import com.drdisagree.uniride.data.dao.ScheduleDao
import com.drdisagree.uniride.data.utils.toSchedule
import com.drdisagree.uniride.data.utils.toScheduleEntity
import com.drdisagree.uniride.domain.models.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl(
    private val dao: ScheduleDao
) : ScheduleRepository {

    override suspend fun insertSchedule(schedule: Schedule) {
        dao.insertSchedule(schedule.toScheduleEntity())
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        dao.deleteSchedule(schedule.toScheduleEntity())
    }

    override suspend fun getAllSchedules(): Flow<List<Schedule>> {
        return dao.getAllSchedules().map { scheduleEntity ->
            scheduleEntity.map { it.toSchedule() }
        }
    }
}