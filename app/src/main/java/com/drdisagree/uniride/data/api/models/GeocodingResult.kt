package com.drdisagree.uniride.data.api.models

import com.google.gson.annotations.SerializedName

data class GeocodingResult(
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>
)