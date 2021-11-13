package com.krishe.govern.views.reports

interface ReportsICallBack {
    fun onError(msg: String)
    fun onPrHide()
    fun onPrShow()
    fun onSuccess(list: List<ReportsItemModel>)
    fun onRemoveSuccess(msg: String)
}
