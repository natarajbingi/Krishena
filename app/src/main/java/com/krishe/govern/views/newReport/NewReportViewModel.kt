package com.krishe.govern.views.newReport

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.krishe.govern.networks.NetWorkCall
import com.krishe.govern.networks.imgAzure.ImageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.Executors

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */

class NewReportViewModel(application: Application) : AndroidViewModel(application),
    NewReportCallBack {

    var defaultData = mutableListOf(
        NameImageModel(
            0,
            "",
            "Top View",
            true
        ),
        NameImageModel(
            1,
            "",
            "Front View",
            true
        ),
        NameImageModel(
            2,
            "",
            "Left View",
            true
        ),
        NameImageModel(
            3,
            "",
            "Right View",
            true
        ),
        NameImageModel(
            4,
            "",
            "Back View",
            true
        )
    )

    lateinit var view: NewReportCallBack
    var count = 0
    var countAzr = 0
    var countAzrVV = MutableLiveData<Int>()


    fun onViewAvailable(v: NewReportCallBack) {
        view = v
    }
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

    fun imageUploadSet(context: Context): Boolean {
        view.onPrShow()
        var boo = false
        Executors.newSingleThreadExecutor().execute {
            val mainHandler = Handler(Looper.getMainLooper())
            //sync calculations
            mainHandler.post {
                if (count == defaultData.size) {
                    //Log.e("TAG", "imageUploadSet:defaultData $defaultData")
                    boo = true
                } else {
                    if (defaultData.get(count).imgPath.contains("/")) { // to check whether the img already uploaded or not..
                        val uri = File(defaultData.get(count).imgPath).toUri() //Uri.parse()
                        val uploadedImage = imageUploadAzure(uri, context, count)
                        // defaultData.get(count).imgPath = uploadedImage
                    } else {
                        countAzr++
                        countAzrVV.postValue(countAzr)
                    }
                    count++
                    //Log.e("TAG", "imageUploadSet:count $count : $countAzr")
                    imageUploadSet(context)
                    boo = false
                }
            }
        }
        return boo
    }

    private fun imageUploadAzure(imageUri: Uri, context: Context, count: Int): String {
        var imageName = "Err"
        try {
            //Log.e("TAG", "UploadImage:imageUri $imageUri")
            val imageStream: InputStream = context.contentResolver.openInputStream(imageUri)!!
            val imageLength = imageStream.available()
            val th = Thread {
                try {
                    imageName = ImageManager.UploadImage(imageStream, imageLength)
                    //Log.e("TAGSuccess", "Image Uploaded SuccessfuLly. Name = $imageName count-${count}")
                    defaultData.get(count).imgPath = imageName
                    countAzr++
                    countAzrVV.postValue(countAzr)
                } catch (ex: Exception) {
                    val exceptionMessage = ex.message
                    Log.e("TAGErr", "$exceptionMessage")
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

    fun addImplement(newReportModelReq: NewReportModelReq) {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            val g = Gson()
            NetWorkCall.addImplement(
                this@NewReportViewModel,
                JSONObject(g.toJson(newReportModelReq))
            )
        }
    }

    fun updateImplement(newReportModelReq: NewReportModelReq) {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            val g = Gson()
            NetWorkCall.updateImplement(
                this@NewReportViewModel,
                JSONObject(g.toJson(newReportModelReq))
            )
        }
    }

    override fun onError(msg: String) {
        view.onPrHide()
        view.onError(msg)
    }

    override fun onPrHide() {
        view.onPrHide()
    }

    override fun onPrShow() {
        view.onPrShow()
    }

    override fun onSuccess(msg: String) {
        view.onPrHide()
        view.onSuccess(msg)
    }

}