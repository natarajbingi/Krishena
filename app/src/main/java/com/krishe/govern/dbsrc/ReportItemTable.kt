package com.krishe.govern.dbsrc

import androidx.room.Entity
import androidx.room.PrimaryKey
//import com.krishe.govern.views.newReport.NameImageModel

@Entity(tableName = "report_item_table")
data class ReportItemTable(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var userId: String, // here to --
    var implementID: String,
    var implementName: String,
    var reportTypeID: String,
    var reportTypeName: String,
    var ownerShip: String,
    var latitude: Double,
    var longitude: Double, // -- here capture in initReport
    var nameImageModel: String,// Json Array store images list
    var currentImplementStatus: String, // radio button status
    var reportComment: String,
    var reportSubmitted: String = "N"
)
