package com.krishe.govern.views.newReport

import android.os.Parcel
import android.os.Parcelable

data class NewReportModelReq(var implementID: String) : Parcelable {
    var implementName: String = ""
    var reportTypeID: String = ""
    var reportTypeName: String = ""
    var ownerShip: String = ""
    var latitude: String = "0.0"
    var longitude: String = "0.0"
    var nameImageModel: List<NameImageModel> = emptyList()
    var currentImplementStatus: String = ""
    var reportComment: String = ""

    constructor(parcel: Parcel) : this(parcel.readString().toString()) {
        implementName = parcel.readString().toString()
        reportTypeID = parcel.readString().toString()
        reportTypeName = parcel.readString().toString()
        ownerShip = parcel.readString().toString()
        currentImplementStatus = parcel.readString().toString()
        reportComment = parcel.readString().toString()
        latitude = parcel.readString().toString()
        longitude = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(implementID)
        parcel.writeString(implementName)
        parcel.writeString(reportTypeID)
        parcel.writeString(reportTypeName)
        parcel.writeString(ownerShip)
        parcel.writeString(currentImplementStatus)
        parcel.writeString(reportComment)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewReportModelReq> {
        override fun createFromParcel(parcel: Parcel): NewReportModelReq {
            return NewReportModelReq(parcel)
        }

        override fun newArray(size: Int): Array<NewReportModelReq?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return  "NewReportModelReq(implementID=$implementID, implementName='$implementName'," +
                " reportTypeID='$reportTypeID'," +
                " reportTypeName='$reportTypeName'," +
                " ownerShip='$ownerShip'," +
                " latitude='$latitude'" +
                " longitude='$longitude'" +
                " currentImplementStatus='$currentImplementStatus'," +
                " reportComment='$reportComment'," +
                " nameImageModel='$nameImageModel'" +
                ")"
    }
}