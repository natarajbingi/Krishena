package com.krishe.govern.networks

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ANRequest
import com.androidnetworking.common.ANResponse
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.google.gson.Gson
import com.jacksonandroidnetworking.JacksonParserFactory
import com.krishe.govern.models.Data
import com.krishe.govern.models.ImplementsDataRes
import com.krishe.govern.utils.User
import com.krishe.govern.views.home.InitIReportCallBack
import com.krishe.govern.views.home.InitIReportCallBackReturn
import com.krishe.govern.views.newReport.NewReportCallBack
import com.krishe.govern.views.newReport.NewReportModelReq
import com.krishe.govern.views.reports.ReportsICallBack
import com.krishe.govern.views.reports.ReportsItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class NetWorkCall {
    companion object {
        val cacheSize = 5 * 1024 * 1024 // 5 MB size

        const val BASE_URL = "https://krishe-rental.azurewebsites.net"
        const val BASE_URL_ = "http://krisheapp.pythonanywhere.com"
        val API_KEY = "ApiKey"
        val KEY = "WqvL33dHhSNs1AjUZPJjv8zJ8J1iD3qE"
        val HEADER_CACHE_CONTROL = "Cache-Control"
        val HEADER_PRAGMA = "Pragma"
//        val bodyParameterMap: MutableMap<String, String> = mutableMapOf()

        fun NetworkingSetup(context: Context) {

            // Adding an Network Interceptor for Debugging purpose :
            val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
                .addNetworkInterceptor(networkInterceptor())
                .addInterceptor(httpLoggingInterceptor())
                .build();
            AndroidNetworking.initialize(context, okHttpClient);
            AndroidNetworking.setParserFactory(JacksonParserFactory())

        }

        private fun networkInterceptor(): Interceptor {
            return Interceptor { chain ->
                Timber.d("network interceptor: called.")
                val response = chain.proceed(chain.request())
                val cacheControl: CacheControl = CacheControl.Builder()
                    .maxAge(5, TimeUnit.SECONDS)
                    .build()
                response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build()
            }
        }

        private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        Timber.d("log: http log: $message")
                    }
                })
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return httpLoggingInterceptor
        }

        private fun getANReqAzure(url: String): ANRequest<out ANRequest<*>> {
            return AndroidNetworking.get("$BASE_URL$url")
                .addHeaders("Content-Type", "application/json")
                .addHeaders(API_KEY, KEY)
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
        }

        private fun getANReqPython(url: String): ANRequest<out ANRequest<*>> {
            return AndroidNetworking.get("$BASE_URL_$url")
                .addHeaders("Content-Type", "application/json")
                //.addHeaders(API_KEY, KEY)
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
        }

        private fun postANReqPythonJson(
            url: String,
            jsonObject: JSONObject
        ): ANRequest<out ANRequest<*>> {
            return AndroidNetworking.post("$BASE_URL_$url")
                .addHeaders("Content-Type", "application/json")
                //.addHeaders(API_KEY, KEY)
                .addHeaders("Accept", "application/json")
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
        }

        suspend fun getDefaultImplement(v: InitIReportCallBackReturn) {
            var implementsDataRes: ImplementsDataRes? = null
            val data: MutableList<Data> = mutableListOf()
            return withContext(Dispatchers.IO) {
                getANReqAzure("/governance/implements/")
                    .getAsJSONObject(object : JSONArrayRequestListener, JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject?) {
                            if (response?.getBoolean("status") == true && response.getString("message") == "Success") {
                                val dataJSONArray: JSONArray = response.getJSONArray("data")
                                for (i in 0 until (dataJSONArray.length())) {
                                    val dataObj = dataJSONArray.getJSONObject(i)
                                    /* //val dealershipDetails = dataObj.getJSONObject("dealership_details")
                                                 //val dealership_name = dataObj.optString("dealership_name")
                                                 val dealership = DealershipDetails(
                                                     dealership_name,
                                                     dealershipDetails.optString(dealership_name)
                                                 )*/
                                    data.add(
                                        Data(
                                            dataObj.optInt("id"),
                                            dataObj.optString("implement_type"),
                                            dataObj.optString("category"),
                                            dataObj.optInt("price_per_acre"),
                                            dataObj.optInt("price_per_day"),
                                            dataObj.optInt("price_per_hour"),
                                            dataObj.optString("implement_category"),
                                            dataObj.optString("implement_code"),
                                            dataObj.optString("dealership_name"),
                                            dataObj.optInt("dealer_phone"),
                                            dataObj.optString("center"),
                                            null // dealership
                                        )
                                    )
                                }
                                implementsDataRes = ImplementsDataRes(true, "Success", data)
                            } else {
                                implementsDataRes = ImplementsDataRes(false, "Failed", data)
                            }
                            implementsDataRes?.let { v.onSuccessImplementList(it) }
                            // Log.e("TAG", "onResponseJSONObject: ${response.toString()}")
                        }

                        override fun onResponse(response: JSONArray?) {
                            // Log.e("TAG", "onResponseJSONArray: ${response.toString()}")
                            implementsDataRes = ImplementsDataRes(false, "Failed", data)
                            implementsDataRes?.let { v.onSuccessImplementList(it) }
                        }

                        override fun onError(anError: ANError?) {
                            Log.e("TAG", "anError: ${anError?.localizedMessage.toString()}")
                            implementsDataRes = ImplementsDataRes(
                                false,
                                "anError: ${anError?.localizedMessage.toString()}",
                                data
                            )
                            implementsDataRes?.let { v.onSuccessImplementList(it) }
                        }
                    })

                //return@withContext implementsDataRes
            }
        }

        suspend fun getMySavedImplements(v: ReportsICallBack) {
            var reportsItemModel: MutableList<ReportsItemModel> = mutableListOf()
            val data: MutableList<Data> = mutableListOf()
            val bodyParamJSON = JSONObject()
            bodyParamJSON.put("userID", "10")
            Log.e("TAG", "bodyParamJSON: $bodyParamJSON")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson("/get_implement", bodyParamJSON)
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            Log.e("TAG", "onResponseJSONObject: ${response.toString()}")
                            val gson = Gson()
                            val data: Array<ReportsItemModel> = gson.fromJson(
                                response.toString(),
                                Array<ReportsItemModel>::class.java
                            )
                            reportsItemModel.addAll(data)
                            v.onSuccess(reportsItemModel)
                        }

                        override fun onError(anError: ANError?) {
                            Log.e("TAG", "anError: ${anError?.localizedMessage.toString()}")
                            v.onError("Failed")
                        }
                    })
                //return@withContext implementsDataRes
            }
        }

        suspend fun addImplement(v: NewReportCallBack, newReportModelReq: JSONObject) {
            //val jArray = JSONArray(newReportModelReq.optString("nameImageModel"))
            //newReportModelReq.put("nameImageModel", jArray.toString())
            Log.e("TAG", "newReportModelReq: $newReportModelReq")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson("/add_implement", newReportModelReq)

                    .getAsString(object : StringRequestListener {
                        override fun onResponse(response: String?) {
                            Log.e("TAG", "StringRequestListener: ${response.toString()}")

                            v.onSuccess(response.toString())
                        }

                        override fun onError(anError: ANError?) {
                            Log.e("TAG", "anError: ${anError?.localizedMessage.toString()}")
                            v.onError("Failed")
                        }
                    })
                //return@withContext implementsDataRes
            }
        }

        suspend fun updateImplement(v: NewReportCallBack, newReportModelReq: JSONObject) {
            //val jArray = JSONArray(newReportModelReq.optString("nameImageModel"))
            //newReportModelReq.put("nameImageModel", jArray.toString())
            Log.e("TAG", "newReportModelReq: $newReportModelReq")
           /* return withContext(Dispatchers.IO) {
                postANReqPythonJson("/add_implement", newReportModelReq)

                    .getAsString(object : StringRequestListener {
                        override fun onResponse(response: String?) {
                            Log.e("TAG", "StringRequestListener: ${response.toString()}")

                            v.onSuccess(response.toString())
                        }

                        override fun onError(anError: ANError?) {
                            Log.e("TAG", "anError: ${anError?.localizedMessage.toString()}")
                            v.onError("Failed")
                        }
                    })
                //return@withContext implementsDataRes
            }*/
            v.onSuccess("newReportModelReq")
        }

    }// companion object over

    private fun convertCode1() {
        AndroidNetworking.post("https://www.yantralive.com/newapi/email_change.php")
            .addHeaders("Mobile", "8527801400")
            .addHeaders("Content-Type", "application/x-www-form-urlencoded;charset=UTF8")
            .addHeaders("Authorization", "Bearer 2d71d4f37225f16f317b79dbb89cf1e79e66681f")
            .setContentType("application/json; charset=utf-8")
            .setTag("convert")
            .setPriority(Priority.MEDIUM)
            .addUrlEncodeFormBodyParameter("newmail", "jmi.mohsin1@gmail.com")
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    println(response.toString())
                }

                override fun onError(error: ANError?) {
                    println(error.toString())
                }
            })
    }

}

