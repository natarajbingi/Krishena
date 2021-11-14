package com.krishe.govern.views.reports

import com.krishe.govern.views.newReport.NewReportModelReq

interface OnReportItemClickListener {
    fun onItemClick(item: NewReportModelReq, position: Int)

    fun onItemLongClick(item: NewReportModelReq, position: Int)
}