package com.drdisagree.uniride.data.api.models

import com.google.gson.annotations.SerializedName

data class AddressComponent(
    @SerializedName("short_name")
    val shortName: String,
    val types: List<String>
)