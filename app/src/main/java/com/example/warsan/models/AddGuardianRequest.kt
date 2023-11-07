package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class AddGuardianRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("location") val location: Int
)
