package com.krishe.govern.views.reports

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
    val createdDate: String
)