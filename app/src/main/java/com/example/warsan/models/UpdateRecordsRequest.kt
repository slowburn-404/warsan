package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class UpdateRecordsRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("vaccineadministration_set") val vaccineAdministrationSet: List<VaccineAdministrationSet>,
    @SerializedName("status") val status: String,
    @SerializedName("next_date_of_administration") val nextDateOfAdministration: String,
    @SerializedName("child") val child: Int
)

data class VaccineAdministrationSet(
    @SerializedName("vaccine") val vaccine: Int,
    @SerializedName("date_of_administration") val dateOfAdministration: String
)
