package com.krishe.govern.views.home

import com.krishe.govern.models.ImplementsDataRes

interface InitIReportCallBack {
    fun onError(msg: String)
    fun onPrHide()
    fun onPrShow()
    fun onSuccessImplementList(list: ImplementsDataRes)
}