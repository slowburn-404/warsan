package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class LogInRequest(
    @SerializedName("password") var password: String,
    @SerializedName("phone_number") var phoneNumber: String

)