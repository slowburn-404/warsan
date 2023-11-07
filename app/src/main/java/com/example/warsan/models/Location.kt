package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("id") val id: Int,
    @SerializedName("region") val region: String,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double
)

data class GetLocationResponse(
    @SerializedName("locations") val locations: List<Location>
)
