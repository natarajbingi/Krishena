package com.krishe.govern.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.krishe.govern.BuildConfig
import com.krishe.govern.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object KrisheUtils {
        const val oldProcessingDate = "oldProcessingDate"
        const val userID = "userID"
        const val implementListSession = "implementListSession"
        const val locationSwitch = "locationSwitch"
        const val KeepMeSigned = "KeepMeSigned"

        fun setSpinnerItems(context: Context, spinner: Spinner, spinnerArray: Array<String>) {

            val aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerArray)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(aa)
        }

        fun checkSinglePermission(activity: Activity, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun toastAction(context: Context, string: String) {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        }

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return LocationManagerCompat.isLocationEnabled(locationManager)
        }

        fun oneDigToTwo(value: Int): String {
            var dd = ""
            dd = if (value < 10) {
                "0$value"
            } else {
                value.toString() + ""
            }
            return dd
        }

        @SuppressLint("SimpleDateFormat")
        fun dateTime(string: String): String {
            val dateFormat = if (string == "NOW") {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            } else {
                SimpleDateFormat("yyyy-MM-dd")
            }
            val date = Date()
            return dateFormat.format(date).toString()
        }

        @SuppressLint("SimpleDateFormat")
        fun dateFLYMTime(string: String): String {
            val date = Date()
            val dateFormat =SimpleDateFormat("yyyy-MM")
            val dateYYFormat =SimpleDateFormat("yyyy-")
            return when (string) {
                "FDY" -> {
                    dateYYFormat.format(date).toString()+"01-01"
                }
                "LDY" -> {
                    dateYYFormat.format(date).toString()+"12-31"
                }
                "FDM" -> {
                    dateFormat.format(date).toString()+"-01"
                }
                "LDM" -> {
                    dateFormat.format(date).toString()+"-"+ getLastDayOf(date.month,date.year)
                }
                else -> dateFormat.format(date).toString()
            }
        }

        fun getLastDayOf(month: Int, year: Int): String {
            return when (month) {
                Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> "30"
                Calendar.FEBRUARY -> {
                    if (year % 4 == 0) {
                        "29"
                    } else "28"
                }
                else -> "31"
            }
        }
        /* Global Alertdialog for application*/
        fun alertDialogShowOK(
            context: Context,
            message: String,
            icon: Int,
            clickListener: DialogInterface.OnClickListener
        ) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setIcon(icon)
            alertDialog.setTitle(context.resources.getString(com.krishe.govern.R.string.app_name))
            alertDialog.setMessage(message)
            alertDialog.setNegativeButton(
                "OK", clickListener
            )
            val alert = alertDialog.create()

            // show it
            alert.show()
        }

        /* Global Alertdialog for application*/
        fun alertDialogShowYesNo(
            context: Context,
            msg: String,
            clickListener: DialogInterface.OnClickListener
        ) {
            AlertDialog.Builder(context)
                .setIcon(com.krishe.govern.R.mipmap.ic_launcher)
                .setTitle(com.krishe.govern.R.string.app_name)
                .setMessage(msg)
                .setPositiveButton(
                    context.resources.getString(com.krishe.govern.R.string.yes),
                    clickListener
                )
                .setNegativeButton(context.resources.getString(com.krishe.govern.R.string.no), null)
                .show()
        }


        fun saveImage(context: Context?, myBitmap: Bitmap): String? {
            val bytes = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            val imgDirectory = File(
                context?.getExternalFilesDir("KrishE-World").toString() + "/KrishE-World"
                //Environment.getExternalStorageDirectory()

            )
            // have the object build the directory structure, if needed.
            if (!imgDirectory.exists()) {
                Log.d("dirrrrrr", "" + imgDirectory.mkdirs())
                imgDirectory.mkdirs()
            }
            try {
                val f = File(imgDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg")
                f.createNewFile() //give read write permission
                val fo = FileOutputStream(f)
                fo.write(bytes.toByteArray())
                MediaScannerConnection.scanFile(
                    context, arrayOf(f.path), arrayOf("image/jpeg"), null
                )
                fo.close()
                Log.d("TAG", "File Saved::--->" + f.absolutePath)
                return f.absolutePath
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            return ""
        }

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val n = cm.activeNetwork
                if (n != null) {
                    val nc = cm.getNetworkCapabilities(n)
                    //It will check for both wifi and cellular network
                    return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                }
                return false
            } else {
                val netInfo = cm.activeNetworkInfo
                return netInfo != null && netInfo.isConnectedOrConnecting
            }
        }

        fun logPrint(call: String, req: Any?, res: Any?) {
            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                val g = Gson()
                Log.d("Request", call)
                req != null ?: Log.d("LogReq", g.toJson(req))
                res != null ?: Log.d("LogRes", g.toJson(res))
            }
        }
        fun popUpImg(
            context: Context?,
            uri: Uri?,
            ImgCredit: String?,
            encodedImgORUrl: String?,
            bitmap: Bitmap?,
            Type: String
        ): Dialog? {
            val dialog = Dialog(context!!, R.style.Base_ThemeOverlay_AppCompat_Light)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.photo_popup)
            val photoId = dialog.findViewById<View>(R.id.photoId) as RelativeLayout

            //        RelativeLayout signId = (RelativeLayout) dialog.findViewById(R.id.signId);
            //        signId.setVisibility(View.GONE);
            photoId.visibility = View.VISIBLE
            val closeImgPopUp = dialog.findViewById<View>(R.id.closeImgPopUp) as ImageView
            val imgPopup = dialog.findViewById<View>(R.id.imgPopup) as ImageView
            val headingText = dialog.findViewById<View>(R.id.headingText) as TextView
            headingText.text = ImgCredit
            closeImgPopUp.setOnClickListener { dialog.dismiss() }
            dialog.setCancelable(true)
            if (Type == "URI") {
                imgPopup.setImageURI(uri)
            } else if (Type == "NOT_URL") {
                val bytes = Base64.decode(encodedImgORUrl, Base64.DEFAULT)
                val bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imgPopup.setImageBitmap(bitmap1)
            } else if (Type == "bitMap") {
                imgPopup.setImageBitmap(bitmap)
            } else { //if (!Type.equals("URL")) {
                try {
                    // Loads given image
                    //  int size = (int) Math.ceil(Math.sqrt(800 * 600));
                    /*Picasso.get()
                        .load(encodedImgORUrl) // .transform(new BitmapTransform(800, 600))
                        // .resize(size, size)
                        // .centerInside()
                        // .noPlaceholder()
                        .placeholder(R.drawable.loader)
                        .error(R.drawable.load_failed)
                        .into(imgPopup)*/
                    Glide.with(context)
                        .load(encodedImgORUrl)
                        //.centerCrop()
                        .placeholder(com.krishe.govern.R.drawable.loader)
                        .error(com.krishe.govern.R.drawable.blankimge)
                        .into(imgPopup)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            dialog.show()
            return dialog
        }


}