package com.krishe.govern.views.reports

import com.krishe.govern.views.newReport.NewReportModelReq

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
interface OnReportItemClickListener {
    fun onItemClick(item: NewReportModelReq, position: Int)

    fun onItemLongClick(item: NewReportModelReq, position: Int)
}