package com.krishe.govern.views.home

import com.krishe.govern.models.ImplementsDataRes
/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
interface InitIReportCallBackReturn {
    fun onError(msg: String)
    fun onSuccessImplementList(list: ImplementsDataRes)
    fun onSuccessStatistics(msg: String)
}