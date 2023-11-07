package com.example.warsan.models

import android.os.Parcel
import android.os.Parcelable

data class AddChildResponseParcelable(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String
    // Add other fields as needed
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
        // Read other fields from the parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(dateOfBirth)
        // Write other fields to the parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddChildResponseParcelable> {
        override fun createFromParcel(parcel: Parcel): AddChildResponseParcelable {
            return AddChildResponseParcelable(parcel)
        }

        override fun newArray(size: Int): Array<AddChildResponseParcelable?> {
            return arrayOfNulls(size)
        }
    }
}

