package com.drdisagree.uniride.data.models

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<Result>,
    val status: String
)

data class Result(
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>
)

data class AddressComponent(
    @SerializedName("short_name")
    val shortName: String,
    val types: List<String>
)