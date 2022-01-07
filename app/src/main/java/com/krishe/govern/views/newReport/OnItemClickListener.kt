package com.krishe.govern.views.newReport

import android.graphics.Bitmap

interface OnItemClickListener {
    fun onItemClick(item: NameImageModel, position: Int)

    fun onItemRemove(item: NameImageModel, position: Int)
    fun onItemZoom(bitmap : Bitmap)
}