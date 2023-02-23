package com.yz.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class Members(
    val memberImage:  String = "",
    val memberName: String = "",
    val memberEmail: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(memberImage)
        parcel.writeString(memberName)
        parcel.writeString(memberEmail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Members> {
        override fun createFromParcel(parcel: Parcel): Members {
            return Members(parcel)
        }

        override fun newArray(size: Int): Array<Members?> {
            return arrayOfNulls(size)
        }
    }
}