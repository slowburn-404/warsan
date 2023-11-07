package com.example.warsan.models

import com.google.gson.annotations.SerializedName


data class ErrorResponse(
    @SerializedName("phone_number")
    val phoneNumber: List<String>
)

data class SuccessResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("location_name")
    val locationName: String,
    @SerializedName("location")
    val location: Int
)
