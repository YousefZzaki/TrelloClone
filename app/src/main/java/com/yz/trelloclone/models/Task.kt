package com.yz.trelloclone.models

import android.os.Parcel
import android.os.Parcelable

data class Task(
    val taskTitle: String = "",
    val createdBy: String = "",
    var taskCards: ArrayList<Card> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Card.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(taskTitle)
        parcel.writeString(createdBy)
        parcel.writeTypedList(taskCards)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
