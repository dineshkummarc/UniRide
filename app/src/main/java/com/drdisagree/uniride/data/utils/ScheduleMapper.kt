package com.drdisagree.uniride.data.utils

import com.drdisagree.uniride.data.entities.ScheduleEntity
import com.drdisagree.uniride.domain.models.Schedule

fun ScheduleEntity.toSchedule(): Schedule {
    return Schedule(
        id = id,
        busName = busName,
        departureFrom = departureFrom,
        departureFor = departureFor,
        departureTime = departureTime,
        busCategory = busCategory
    )
}

fun Schedule.toScheduleEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = id,
        busName = busName,
        departureFrom = departureFrom,
        departureFor = departureFor,
        departureTime = departureTime,
        busCategory = busCategory
    )
}