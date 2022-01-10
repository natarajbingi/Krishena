package com.krishe.govern.views.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.krishe.govern.networks.NetWorkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class ReportsViewModel(application: Application) : AndroidViewModel(application), ReportsICallBack {
    // TODO: Implement the ViewModel
    lateinit var view: ReportsICallBack
    val data: MutableLiveData<List<ReportsItemModel>> = MutableLiveData<List<ReportsItemModel>>()

    fun onViewAvailable(v: ReportsICallBack) {
        view = v
    }

    fun getData(): LiveData<List<ReportsItemModel>> {
        return data
    }

    fun getReportsCall(paramJson: JSONObject) {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            NetWorkCall.getMyImplements(this@ReportsViewModel, paramJson)
        }
    }

    fun deleteReportsCall(paramJson: JSONObject) {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            NetWorkCall.removeImplement(this@ReportsViewModel, paramJson)
        }
    }

    override fun onError(msg: String) {
        view.onPrHide()
        view.onError(msg)
    }

    override fun onSuccess(list: List<ReportsItemModel>) {
        view.onPrHide()
        view.onSuccess(list)
        // Log.e(ReportsFragment.TAG, "onSuccess: ${list[0].reportName}" )
        data.postValue(list)
    }

    override fun onRemoveSuccess(msg: String) {
        view.onPrHide()
        view.onRemoveSuccess(msg)

    }

    override fun onPrHide() {
    }

    override fun onPrShow() {
    }

    override fun onCleared() {
        super.onCleared()
    }
}