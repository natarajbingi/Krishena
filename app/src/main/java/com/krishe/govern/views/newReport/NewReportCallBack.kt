package com.krishe.govern.views.newReport

interface NewReportCallBack {
    fun onError(msg: String)
    fun onPrHide()
    fun onPrShow()
    fun onSuccess(msg: String)
}
