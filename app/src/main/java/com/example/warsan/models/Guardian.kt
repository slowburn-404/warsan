package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class Guardian(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("status") val status: String,
    @SerializedName("location_name") val locationName: String,
    @SerializedName("location") val location: Int
)

data class ChildDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("status") val status: String,
    @SerializedName("guardian_name") val guardianName: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("location_name") val locationName: String,
    @SerializedName("guardian") val guardian: Int
)

data class GuardianResponse(
    @SerializedName("guardian") val guardian: Guardian,
    @SerializedName("children") val children: List<ChildDetails>
)
