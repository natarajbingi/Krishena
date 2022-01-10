package com.krishe.govern.views.reports

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
interface ReportsICallBack {
    fun onError(msg: String)
    fun onPrHide()
    fun onPrShow()
    fun onSuccess(list: List<ReportsItemModel>)
    fun onRemoveSuccess(msg: String)
}
