package com.krishe.govern.networks

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ANRequest
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
import com.krishe.govern.views.home.InitIReportCallBackReturn
import com.krishe.govern.views.newReport.NewReportCallBack
import com.krishe.govern.views.reports.ReportsICallBack
import com.krishe.govern.views.reports.ReportsItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class NetWorkCall {
    companion object {
        // private val cacheSize = 5 * 1024 * 1024 // 5 MB size

        private const val BASE_URL = "https://krishe-rental.azurewebsites.net"
        private const val BASE_URL_ = "http://krisheapp.pythonanywhere.com"
        private const val API_KEY = "ApiKey"
        private const val KEY = "WqvL33dHhSNs1AjUZPJjv8zJ8J1iD3qE"
        private const val HEADER_CACHE_CONTROL = "Cache-Control"
        private const val HEADER_PRAGMA = "Pragma"
        private const val governanceImplements = "/governance/implements/"
        private const val GET_IMPLEMENT = "/get_implement"
        private const val ADD_IMPLEMENT = "/add_implement"
        private const val UPDATE_IMPLEMENT = "/update_implement"
        private const val REMOVE_IMPLEMENT = "/remove_implement"
        private const val DELETE_IMPLEMENT = "/delete_implement"

        fun networkingSetup(context: Context) {

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
                Log.e("TAG", "network interceptor: called.")
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
                        Log.e("TAG", "http log: $message")
                    }
                })
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return httpLoggingInterceptor
        }

        private fun getANReqAzure(url: String): ANRequest<out ANRequest<*>> {
            Log.e("TAG", "postANReqPythonJson: $BASE_URL$url", )
            return AndroidNetworking.get("$BASE_URL$url")
                .addHeaders("Content-Type", "application/json")
                .addHeaders(API_KEY, KEY)
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
        }

        private fun postANReqPythonJson(url: String, jsonObject: JSONObject): ANRequest<out ANRequest<*>> {
            Log.e("TAG", "postANReqPythonJson: $BASE_URL_$url", )
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
                getANReqAzure(governanceImplements)
                    .getAsJSONObject(object : JSONArrayRequestListener, JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject?) {
                            if (response?.getBoolean("status") == true && response.getString("message") == "Success") {
                                val dataJSONArray: JSONArray = response.getJSONArray("data")
                                for (i in 0 until (dataJSONArray.length())) {
                                    val dataObj = dataJSONArray.getJSONObject(i)
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

        suspend fun getMyImplements(v: ReportsICallBack, getImplParam : JSONObject) {
            val reportsItemModel: MutableList<ReportsItemModel> = mutableListOf()
            Log.e("TAG", "getMyImplements param: $getImplParam")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson(GET_IMPLEMENT, getImplParam)
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            Log.e("TAG", "onResponseJSONObject: ${response.toString()}")
                            if(response.optString("Success") == "true"){
                                val gson = Gson()
                                val data: Array<ReportsItemModel> = gson.fromJson(
                                    response.optJSONArray("data").toString(),
                                    Array<ReportsItemModel>::class.java
                                )
                                reportsItemModel.addAll(data)
                                v.onSuccess(reportsItemModel)
                            } else {
                                v.onError( response.optString("data").toString())
                            }
                        }

                        override fun onError(anError: ANError?) {
                            Log.e("TAG", "anError: ${anError?.localizedMessage.toString()}")
                            v.onError("Failed")
                        }
                    })
            }
        }

        suspend fun addImplement(v: NewReportCallBack, addNewParam: JSONObject) {
            Log.e("TAG", "add newReportModelReq: $addNewParam")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson(ADD_IMPLEMENT, addNewParam)

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
            }
        }

        suspend fun updateImplement(v: NewReportCallBack, updateParam: JSONObject) {
            Log.e("TAG", "update newReportModelReq: $updateParam")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson(UPDATE_IMPLEMENT, updateParam)

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
            }
        }

        // update deletion for user but keep in server DataBase
        suspend fun removeImplement(v: ReportsICallBack, removeParam: JSONObject) {
            Log.e("TAG", "remove newReportModelReq: $removeParam")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson(REMOVE_IMPLEMENT, removeParam)

                    .getAsString(object : StringRequestListener {
                        override fun onResponse(response: String?) {
                            Log.e("TAG", "StringRequestListener: ${response.toString()}")

                            v.onRemoveSuccess(response.toString())
                        }

                        override fun onError(anError: ANError?) {
                            Log.e("TAG", "anError: ${anError?.localizedMessage.toString()}")
                            v.onError("Failed")
                        }
                    })
            }
        }

        // delete from server DataBase
        suspend fun deleteImplement(v: NewReportCallBack, deleteParam: JSONObject) {
            Log.e("TAG", "delete newReportModelReq: $deleteParam")
            return withContext(Dispatchers.IO) {
                postANReqPythonJson(DELETE_IMPLEMENT, deleteParam)
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
            }
        }

    }// companion object over

}

