package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class AddChildResponse(
    @SerializedName("age") val age: Int,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("guardian") val guardian: Int,
    @SerializedName("guardian_name") val guardianName: String,
    @SerializedName("id") val id: Int,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("location_name") val locationName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("status") val status: String
)
