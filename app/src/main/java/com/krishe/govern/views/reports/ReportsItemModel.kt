package com.krishe.govern.views.reports

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
data class ReportsItemModel(
    val id: Int,
    val userID: String,
    val currentImplementStatus: String,
    val implementID: String,
    val implementName: String,
    val isEditable: String,
    val latitude: String,
    val longitude: String,
    val nameImageModel: String,
    val ownerShip: String,
    val reportComment: String,
    val reportTypeId: String,
    val reportTypeName: String,
    val reportStatusForApproval: String,
    val createdDate: String,
    var comments: String,
    var location: String
)