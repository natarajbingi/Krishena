package com.krishe.govern.views.newReport

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.krishe.govern.imgAzure.ImageManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

class NewReportViewModel(application: Application) : AndroidViewModel(application) {

    var defaultData = mutableListOf(
        NameImageModel(
            0,
            "/data/user/0/com.krishe.govern/cache/EasyImage/ei_16350615735038942260417884512182.jpg",
            "Top View",
            true
        ),
        NameImageModel(
            1,
            "/data/user/0/com.krishe.govern/cache/EasyImage/ei_16350616013838435685476596135161.jpg",
            "Front View",
            true
        ),
        NameImageModel(
            2,
            "/data/user/0/com.krishe.govern/cache/EasyImage/ei_16350616136107525071678437372477.jpg",
            "Left View",
            true
        ),
        NameImageModel(
            3,
            "/data/user/0/com.krishe.govern/cache/EasyImage/ei_16350616215056256379476617957603.jpg",
            "Right View",
            true
        ),
        NameImageModel(
            4,
            "/data/user/0/com.krishe.govern/cache/EasyImage/ei_16350616333214711314933528090675.jpg",
            "Back View",
            true
        )
    )

    // get edit text layout
    fun getEditTextLayout(context: Context): ConstraintLayout {
        val constraintLayout = ConstraintLayout(context)
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        constraintLayout.layoutParams = layoutParams
        constraintLayout.id = View.generateViewId()

        val textInputLayout = TextInputLayout(context)
        textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
        layoutParams.setMargins(
            32.toDp(context),
            8.toDp(context),
            32.toDp(context),
            8.toDp(context)
        )
        textInputLayout.layoutParams = layoutParams
        textInputLayout.hint = "Input name"
        textInputLayout.id = View.generateViewId()
        textInputLayout.tag = "textInputLayoutTag"


        val textInputEditText = TextInputEditText(context)
        textInputEditText.id = View.generateViewId()
        textInputEditText.tag = "textInputEditTextTag"

        textInputLayout.addView(textInputEditText)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        constraintLayout.addView(textInputLayout)
        return constraintLayout
    }

    // extension method to convert pixels to dp
    private fun Int.toDp(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()

    override fun onCleared() {
        super.onCleared()
    }

    var count = 0
    fun imageUploadSet(context: Context): Boolean {
        if (count == defaultData.size) {
            return true
        } else {
            val uri = File(defaultData.get(count).imgPath).toUri() //Uri.parse()
            val uploadedImage = imageUploadAzure(uri, context,count)
           // defaultData.get(count).imgPath = uploadedImage
            count++

            imageUploadSet(context)
            return false
        }
    }

    private fun imageUploadAzure(imageUri: Uri, context: Context, count: Int): String {
        var imageName = "Err"
        try {
            Log.e("TAG", "UploadImage:imageUri $imageUri")
            val imageStream: InputStream = context.contentResolver.openInputStream(imageUri)!!
            val imageLength = imageStream.available()
            //val handler = Handler()
            val th = Thread {
                try {
                    imageName = ImageManager.UploadImage(imageStream, imageLength)
                    //handler.post {
                    Log.e(
                        "TAGSuccess",
                        "Image Uploaded SuccessfuLly. Name = $imageName count-${count}"
                    )
                    defaultData.get(count).imgPath = imageName
                    //}
                } catch (ex: Exception) {
                    val exceptionMessage = ex.message
                    //handler.post {
                    Log.e("TAGErr", "$exceptionMessage")
                    // }
                }
            }
            th.start()
        } catch (ex: Exception) {
            Log.e("TAGException", "${ex.message}")
        }
        return imageName
    }

    fun loadImgViewDwnAzure(image: String, imageView: ImageView) {
        val imageStream = ByteArrayOutputStream()

        val handler = Handler()

        val th = Thread {
            try {
                val imageLength: Long = 0
                ImageManager.GetImage(image, imageStream, imageLength)
                handler.post {
                    val buffer: ByteArray = imageStream.toByteArray()
                    val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                    imageView.setImageBitmap(bitmap)
                }
            } catch (ex: java.lang.Exception) {
                val exceptionMessage = ex.message
                handler.post {
                    Log.e("TAG", "$exceptionMessage")
                }
            }
        }
        th.start()
    }

}