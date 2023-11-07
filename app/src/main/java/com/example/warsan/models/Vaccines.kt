package com.example.warsan.models
import com.google.gson.annotations.SerializedName

data class Vaccines(
    @SerializedName("id")
    val id: Int,
    @SerializedName("vaccine_choice")
    val vaccineChoice: String
)
