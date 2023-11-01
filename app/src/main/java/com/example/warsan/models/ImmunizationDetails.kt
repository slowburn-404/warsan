package com.example.warsan.models

data class ImmunizationDetails(
    val name: String,
    var status: Int? = null,
    val dateTaken: String
)
