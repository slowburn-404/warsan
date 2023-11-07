package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class AddGuardianError(
    @SerializedName("phone_number")
    val phoneNumber: List<String>
)
