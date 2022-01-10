package com.krishe.govern.models

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
data class ImplementsDataRes(
    var status: Boolean,
    var message: String,
    var data: List<Data>
)
data class Data (
    var id : Int,
    var implement_type : String,
    var category : String,
    var price_per_acre : Int,
    var price_per_day : Int,
    var price_per_hour : Int,
    var implement_category : String,
    var implement_code : String,
    var dealership_name : String,
    var dealer_phone : Int,
    var center : String,
    var dealership_details : DealershipDetails?
)

data class DealershipDetails (
    var dealerName : String,
    var dealerCode : String
)
