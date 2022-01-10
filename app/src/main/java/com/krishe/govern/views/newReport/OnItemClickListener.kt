package com.krishe.govern.views.newReport

import android.graphics.Bitmap

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
interface OnItemClickListener {
    fun onItemClick(item: NameImageModel, position: Int)

    fun onItemRemove(item: NameImageModel, position: Int)
    fun onItemZoom(bitmap : Bitmap)
}