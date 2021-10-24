package com.krishe.govern.views.newReport

data class NameImageModel(var imgPosition: Int, var imgPath: String, var imgName: String, var isEditable: Boolean)  {
    override fun toString(): String {
        return "NameImageModel(imgPosition=$imgPosition, imgPath='$imgPath', imgName='$imgName', isEditable='$isEditable')"
    }

}