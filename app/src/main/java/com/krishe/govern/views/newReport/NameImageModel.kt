package com.krishe.govern.views.newReport

import java.io.Serializable

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
data class NameImageModel(var imgPosition: Int, var imgPath: String, var imgName: String, var isEditable: Boolean) : Serializable  {
    override fun toString(): String {
        return "NameImageModel(imgPosition=$imgPosition, imgPath='$imgPath', imgName='$imgName', isEditable='$isEditable')"
    }

}