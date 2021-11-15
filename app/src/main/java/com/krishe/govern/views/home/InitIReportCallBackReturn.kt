package com.krishe.govern.views.home

import com.krishe.govern.models.ImplementsDataRes

interface InitIReportCallBackReturn {
    fun onError(msg: String)
    fun onSuccessImplementList(list: ImplementsDataRes)
    fun onSuccessStatistics(msg: String)
}