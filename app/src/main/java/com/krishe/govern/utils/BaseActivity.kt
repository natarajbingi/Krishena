package com.krishe.govern.utils

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.krishe.govern.R
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile

open class BaseActivity : AppCompatActivity() {

    /*Images related starts*/
    val CHOOSER_PERMISSIONS_REQUEST_CODE = 7459
    val CAMERA_REQUEST_CODE = 7500
    val CAMERA_VIDEO_REQUEST_CODE = 7501
    val GALLERY_REQUEST_CODE = 7502
    val DOCUMENTS_REQUEST_CODE = 7503
    var photos: ArrayList<MediaFile> = ArrayList<MediaFile>()
    var easyImage: EasyImage? = null
    val PHOTOS_KEY = "easy_image_photos_list"

    lateinit var progressDoalog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    open fun showPbar(context: Context) {
        if (progressDoalog == null) {
            progressDoalog = ProgressDialog(context);
            progressDoalog.setTitle(context.getString(R.string.app_name));
            progressDoalog.setCancelable(false);
            progressDoalog.setMessage("Please wait....");
        }
        progressDoalog.show();

    }

    open fun hidePbar() {
        if (progressDoalog != null) {
            if (progressDoalog.isShowing()) {
                progressDoalog.dismiss();
            }
        }
    }

    open fun onSetEasyImg(multiYesNo: Boolean, context: Context) {
        easyImage = EasyImage.Builder(context)
            .setChooserTitle("Pick media")
            .setCopyImagesToPublicGalleryFolder(true)
            //.setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .setFolderName("KrishE-World")
            .allowMultiple(multiYesNo)
            .build()

    }

    open fun selectImage(context: Context?) {
        val items = arrayOf<CharSequence>("Take Photo", "Cancel")//, "Choose from Library"
        val builder = AlertDialog.Builder(
            context!!
        )
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (item == 0) {
                val necessaryPermissions = arrayOf(Manifest.permission.CAMERA)
                if (arePermissionsGranted(necessaryPermissions)) {
                    easyImage!!.openCameraForImage(this@BaseActivity)
                } else {
                    requestPermissionsCompat(necessaryPermissions, CAMERA_REQUEST_CODE)
                }
            }
            /* else if (item == 1) {
                val necessaryPermissions =
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (arePermissionsGranted(necessaryPermissions)) {
                    easyImage!!.openGallery(this@BaseActivity)
                } else {
                    requestPermissionsCompat(
                        necessaryPermissions,
                        GALLERY_REQUEST_CODE
                    )
                }
            }*/
            else if (item == 1) { // 2
                dialog.dismiss()
            }
        }
        builder.show()
    }

    open fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) return false
        }
        return true
    }

    open fun requestPermissionsCompat(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this@BaseActivity, permissions!!, requestCode)
    }

    open fun isLegacyExternalStoragePermissionRequired(): Boolean {
        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return Build.VERSION.SDK_INT < 29 && !permissionGranted
    }
    open fun stringToBitmap(imageString: String?): Bitmap? {
        //decode base64 string to image
        val imageBytes: ByteArray = Base64.decode(imageString, Base64.DEFAULT)
        //image.setImageBitmap(decodedImage);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CHOOSER_PERMISSIONS_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage!!.openChooser(this@BaseActivity)
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage!!.openCameraForImage(this@BaseActivity)
        } else if (requestCode == CAMERA_VIDEO_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage!!.openCameraForVideo(this@BaseActivity)
        } else if (requestCode == GALLERY_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage!!.openGallery(this@BaseActivity)
        } else if (requestCode == DOCUMENTS_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage!!.openDocuments(this@BaseActivity)
        }
    }
}