package com.krishe.govern.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
//import androidx.core.content.ContextCompat.getExternalFilesDirs
import android.os.Environment
import java.io.*
import java.util.*


class ImageFile(val uri: Uri, name: String) {

    val filename: String

    init {
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getStorageDirectory().toString() + "/Documentss")
        } else {
            // ("VERSION.SDK_INT < R")
             File(Environment.getExternalStorageDirectory().toString()+"/Documentss" )
        }
        if (!file.exists()) {
            file.mkdirs()
        }
//        val fileNoMedia = File(file.absolutePath + "/.nomedia")
//        if (!fileNoMedia.exists())
//            fileNoMedia.createNewFile()
        filename = if (name.lowercase(Locale.getDefault()).endsWith(".pdf")) {
            file.absolutePath + "/" + System.currentTimeMillis() + ".pdf"
        } else {
            file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
        }
    }

    @Throws(IOException::class)
    fun copyFileStream(context: Context, uri: Uri): String {
        if (filename.endsWith(".pdf") || filename.endsWith(".PDF")) {
            var ins: InputStream? = null
            var os: OutputStream? = null
            try {
                ins = context.contentResolver.openInputStream(uri)
                os = FileOutputStream(filename)
                val buffer = ByteArray(1024)
                var length: Int = (ins?.read(buffer) ?:0)
                while (length > 0) {
                    os.write(buffer, 0, length);
                    length = ins?.read(buffer)?:0
                }
            } catch (e: Exception) {
                e.printStackTrace();
            } finally {
                ins?.close()
                os?.close()
            }
        } else {
            var ins: InputStream? = null
            var os: OutputStream? = null
            try {
                ins = context.getContentResolver().openInputStream(uri)
                var scaledBitmap: Bitmap? = null
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                var bmp = BitmapFactory.decodeStream(ins, null, options)
                var actualHeight = options.outHeight
                var actualWidth = options.outWidth

                //      max Height and width values of the compressed image is taken as 816x612
                val maxHeight = 816.0f
                val maxWidth = 612.0f
                var imgRatio = (actualWidth / actualHeight).toFloat()
                val maxRatio = maxWidth / maxHeight

                //      width and height values are set maintaining the aspect ratio of the image
                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight
                        actualWidth = (imgRatio * actualWidth).toInt()
                        actualHeight = maxHeight.toInt()
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth
                        actualHeight = (imgRatio * actualHeight).toInt()
                        actualWidth = maxWidth.toInt()
                    } else {
                        actualHeight = maxHeight.toInt()
                        actualWidth = maxWidth.toInt()

                    }
                }

                //      setting inSampleSize value allows to load a scaled down version of the original image
                options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

                //      inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false

                //      this options allow android to claim the bitmap memory if it runs low on memory
                options.inPurgeable = true
                options.inInputShareable = true
                options.inTempStorage = ByteArray(16 * 1024)


                try {
                    //          load the bitmap from its path
                    ins?.close()
                    ins = context.getContentResolver().openInputStream(uri)
                    bmp = BitmapFactory.decodeStream(ins, null, options)
                } catch (exception: OutOfMemoryError) {
                    exception.printStackTrace()

                }

                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
                } catch (exception: OutOfMemoryError) {
                    exception.printStackTrace()
                }

                val ratioX = actualWidth / options.outWidth.toFloat()
                val ratioY = actualHeight / options.outHeight.toFloat()
                val middleX = actualWidth / 2.0f
                val middleY = actualHeight / 2.0f

                val scaleMatrix = Matrix()
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

                val canvas = Canvas(scaledBitmap!!)
                //canvas.matrix = scaleMatrix
                canvas.setMatrix(scaleMatrix)
                if (bmp != null) {
                    canvas.drawBitmap(bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
                }

                os = FileOutputStream(filename)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, os)
                val buffer = ByteArray(1024)
                var length: Int = ins?.read(buffer)?:0
                while (length > 0) {
                    os.write(buffer, 0, length);
                    length = ins?.read(buffer)?:0
                }
            } catch (e: Exception) {
                e.printStackTrace();
            } finally {
                ins?.close()
                os?.close()
            }
        }
        return filename
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }
}