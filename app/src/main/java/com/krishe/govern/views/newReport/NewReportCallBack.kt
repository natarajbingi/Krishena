package com.krishe.govern.views.newReport

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
interface NewReportCallBack {
    fun onError(msg: String)
    fun onPrHide()
    fun onPrShow()
    fun onSuccess(msg: String)
}
