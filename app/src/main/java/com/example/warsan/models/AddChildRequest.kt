package com.example.warsan.models

import com.google.gson.annotations.SerializedName


data class AddChildRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("date_of_birth") val dateOfBirth: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("guardian") val guardian: Int,

)
