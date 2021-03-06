package com.krishe.govern.views.newReport

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
data class NewReportModelReq(var implementID: String) : Parcelable {
    var id: String = ""
    var implementName: String = ""
    var userID: String = ""
    var reportTypeID: String = ""
    var reportTypeName: String = ""
    var ownerShip: String = ""
    var latitude: String = "0.0"
    var longitude: String = "0.0"
    var nameImageModel:  String = "" //List<NameImageModel> = emptyList()
    var currentImplementStatus: String = ""
    var reportComment: String = ""
    var center: String = ""
    var comments: String = ""
    var location: String = ""

    constructor(parcel: Parcel) : this(parcel.readString().toString()) {
        id = parcel.readString().toString()
        implementName = parcel.readString().toString()
        userID = parcel.readString().toString()
        reportTypeID = parcel.readString().toString()
        reportTypeName = parcel.readString().toString()
        ownerShip = parcel.readString().toString()
        latitude = parcel.readString().toString()
        longitude = parcel.readString().toString()
        nameImageModel = parcel.readString().toString()
        currentImplementStatus = parcel.readString().toString()
        reportComment = parcel.readString().toString()
        center = parcel.readString().toString()
        comments = parcel.readString().toString()
        location = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(implementID)
        parcel.writeString(id)
        parcel.writeString(implementName)
        parcel.writeString(userID)
        parcel.writeString(reportTypeID)
        parcel.writeString(reportTypeName)
        parcel.writeString(ownerShip)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(nameImageModel)
        parcel.writeString(currentImplementStatus)
        parcel.writeString(reportComment)
        parcel.writeString(center)
        parcel.writeString(comments)
        parcel.writeString(location)
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
                " id='$id'," +
                " userID='$userID'," +
                " ownerShip='$ownerShip'," +
                " latitude='$latitude'" +
                " longitude='$longitude'" +
                " currentImplementStatus='$currentImplementStatus'," +
                " reportComment='$reportComment'," +
                " nameImageModel='$nameImageModel'" +
                " center='$center'" +
                " comments='$comments'" +
                " location='$location'" +
                ")"
    }
}