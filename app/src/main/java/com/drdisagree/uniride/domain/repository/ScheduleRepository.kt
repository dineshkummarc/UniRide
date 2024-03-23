package com.drdisagree.uniride.domain.repository

import com.drdisagree.uniride.domain.models.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {

    suspend fun insertSchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

    suspend fun getAllSchedules(): Flow<List<Schedule>>
}