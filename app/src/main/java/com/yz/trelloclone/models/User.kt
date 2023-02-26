package com.yz.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val email: String = "",
    val mobile: Long = 0,
    val fcmToken: String = "",
    var isSelected: Boolean = false
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt() != 0
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(email)
        parcel.writeLong(mobile)
        parcel.writeString(fcmToken)
        parcel.writeInt(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    fun Parcel.writeBoolean(flag: Boolean?) {
        when (flag) {
            true -> writeInt(1)
            false -> writeInt(0)
            else -> writeInt(-1)
        }
    }

    fun Parcel.readBoolean(): Boolean? {
        return when (readInt()) {
            1 -> true
            0 -> false
            else -> null
        }
    }

    //  if (a) b else c
}
