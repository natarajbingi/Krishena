package com.krishe.govern.views.newReport

interface OnItemClickListener {
    fun onItemClick(item: NameImageModel, position: Int)

    fun onItemRemove(item: NameImageModel, position: Int)
}