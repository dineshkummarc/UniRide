package com.drdisagree.uniride.data.models

import java.util.UUID

data class Bus(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val time: String,
    val from: String? = null,
    val to: String? = null,
    val departedFrom: String? = null,
    val departedTo: String? = null,
    val departedAt: Long? = null,
    val driver: Driver? = null
)