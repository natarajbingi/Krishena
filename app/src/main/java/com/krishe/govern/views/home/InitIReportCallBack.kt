package com.krishe.govern.views.home

import com.krishe.govern.models.ImplementsDataRes

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
interface InitIReportCallBack {
    fun onError(msg: String)
    fun onPrHide()
    fun onPrShow()
    fun onSuccessImplementList(list: ImplementsDataRes)
    fun onSuccessStatistics(msg: String)
}