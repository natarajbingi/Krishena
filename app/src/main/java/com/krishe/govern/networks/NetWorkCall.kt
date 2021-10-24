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
import com.jacksonandroidnetworking.JacksonParserFactory
import com.krishe.govern.models.Data
import com.krishe.govern.models.ImplementsDataRes
import com.krishe.govern.utils.User
import com.krishe.govern.views.home.InitIReportCallBack
import com.krishe.govern.views.home.InitIReportCallBackReturn
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
        val API_KEY = "ApiKey"
        val KEY = "WqvL33dHhSNs1AjUZPJjv8zJ8J1iD3qE"
        val HEADER_CACHE_CONTROL = "Cache-Control"
        val HEADER_PRAGMA = "Pragma"

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

        private fun getAndroidNetworking(url: String): ANRequest<out ANRequest<*>> {
            return AndroidNetworking.get("$BASE_URL$url")
                .addHeaders("Content-Type", "application/json")
                .addHeaders(API_KEY, KEY)
                .addHeaders("Accept", "application/json")
                .setPriority(Priority.MEDIUM)
                .build()
        }

        suspend fun getImplementList(v: InitIReportCallBackReturn) {
            var implementsDataRes: ImplementsDataRes? = null
            val data: MutableList<Data> = mutableListOf()
            return withContext(Dispatchers.IO) {
                getAndroidNetworking("/governance/implements/")
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
                            //                            Log.e("TAG", "onResponseJSONObject: ${response.toString()}")
                        }

                        override fun onResponse(response: JSONArray?) {
                            //                            Log.e("TAG", "onResponseJSONArray: ${response.toString()}")
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

        fun getImplementLists(v: InitIReportCallBack) {
            AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray?) {
                        Log.e("TAG", "onResponse: ${response.toString()}")
                        val data: MutableList<ReportsItemModel> = mutableListOf<ReportsItemModel>()
                        if (response != null) {
                            for (i in 0 until response.length()) {
                                val res = response.getJSONObject(i)
                                data.add(
                                    ReportsItemModel(
                                        res.getInt("id"),
                                        res.getString("firstname"),
                                        "21-Mar-2021",
                                        res.getString("lastname"),
                                        "Submitted",
                                    )
                                )
                            }
                            // v.onSuccessImplementList(ImplementsDataRes)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        v.onError("onResponse: ${anError.toString()}")
                        Log.e("TAG", "onResponse: ${anError.toString()}")
                        // TODO("Not yet implemented")
                    }

                })
        }

        fun getCall(v: ReportsICallBack) {
            AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray?) {
                        Log.e("TAG", "onResponse: ${response.toString()}")
                        val data: MutableList<ReportsItemModel> = mutableListOf<ReportsItemModel>()
                        if (response != null) {
                            for (i in 0 until response.length()) {
                                val res = response.getJSONObject(i)
                                data.add(
                                    ReportsItemModel(
                                        res.getInt("id"),
                                        res.getString("firstname"),
                                        "21-Mar-2021",
                                        res.getString("lastname"),
                                        "Submitted",
                                    )
                                )
                            }
                            v.onSuccess(data)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        v.onError("onResponse: ${anError.toString()}")
                        Log.e("TAG", "onResponse: ${anError.toString()}")
                        // TODO("Not yet implemented")
                    }

                })
        }

        fun getCallObject(v: ReportsICallBack) {

            val request =
                AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                    .addPathParameter("pageNumber", "0")
                    .addQueryParameter("limit", "10")
                    .build()

            val response: ANResponse<List<User>> =
                request.executeForObjectList(User::class.java) as ANResponse<List<User>>
            if (response.isSuccess) {
                val data: MutableList<ReportsItemModel> = mutableListOf<ReportsItemModel>()
                val users: List<User> = response.result
                for (user in users) {
                    data.add(
                        ReportsItemModel(
                            user.id,
                            user.firstname,
                            "21-Mar-2021",
                            user.lastname,
                            "Submitted",
                        )
                    )
                }
                v.onSuccess(data)
            } else {
                //handle error
                v.onError("error: ")
                Log.e("TAG", "onResponse: ")
            }
        }

        fun getCallObjectpost(v: ReportsICallBack) {

            val jsonObject = JSONObject()
            try {
                jsonObject.put("firstname", "Amit")
                jsonObject.put("lastname", "Shekhar")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            AndroidNetworking.post("https://fierce-cove-29863.herokuapp.com/createUser")
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        // do anything with response
                        Log.e("TAG", "onResponse: ${response.toString()}")
                        val data: MutableList<ReportsItemModel> = mutableListOf<ReportsItemModel>()
                        if (response != null) {
                            for (i in 0 until response.length()) {
                                val res = response.getJSONObject(i)
                                data.add(
                                    ReportsItemModel(
                                        res.getInt("id"),
                                        res.getString("firstname"),
                                        "21-Mar-2021",
                                        res.getString("lastname"),
                                        "Submitted",
                                    )
                                )
                            }
                            v.onSuccess(data)
                        }

                    }

                    override fun onError(error: ANError) {
                        // handle error
                        v.onError("onResponse: ${error.toString()}")
                        Log.e("TAG", "onResponse: ${error.toString()}")
                    }
                })
        }


    }// companion object over

}