package com.krishe.govern.imgAzure

import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AzureConstants {
    companion object {
        const val ACCOUNT_NAME = "AccountName=governance;"
        const val ACCOUNT_ACCESS_KEY = "AccountKey=TSEdwsvNXK8Kkd+ywGJoyNq2MU/Jcb9bc8j308kizn/QPrAlnfzvNFtQM6L3cwLIQEgfWHt1e6NBnrbUG+yEDQ==;"
        const val ENDPOINT_SUFFIX = "EndpointSuffix=core.windows.net"

        const val REQUEST_TAKE_PHOTO = 1
        var mPhotoFileUri: Uri? = null
        var mPhotoFile: File? = null


        // Create a File object for storing the photo
        @Throws(IOException::class)
        private fun createImageFile(): File? {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
        }
    }
}