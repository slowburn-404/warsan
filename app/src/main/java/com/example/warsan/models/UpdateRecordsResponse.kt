package com.example.warsan.models

import com.google.gson.annotations.SerializedName

data class UpdateRecordsResponse(
    @SerializedName("id") val id: Int,

    @SerializedName("vaccineadministration_set") val vaccineAdministrationSet: List<UpdateVaccineAdministrationSet>,

    @SerializedName("child_first_name") val childFirstName: String,

    @SerializedName("child_last_name") val childLastName: String,

    @SerializedName("child_date_of_birth") val childDateOfBirth: String,

    @SerializedName("child_location") val childLocation: String,

    @SerializedName("child_phone_number") val childPhoneNumber: String,

    @SerializedName("status") val status: String,

    @SerializedName("next_date_of_administration") val nextDateOfAdministration: String,

    @SerializedName("child") val child: Int
)

data class UpdateVaccineAdministrationSet(
    @SerializedName("vaccine") val vaccine: Int,

    @SerializedName("date_of_administration") val dateOfAdministration: String
)
