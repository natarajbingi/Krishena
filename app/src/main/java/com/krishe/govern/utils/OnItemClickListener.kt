package com.krishe.govern.utils

import com.krishe.govern.views.newReport.NameImageModel

interface OnItemClickListener {
    fun onItemClick(item: NameImageModel, position: Int)

    fun onItemRemove(item: NameImageModel, position: Int)
}