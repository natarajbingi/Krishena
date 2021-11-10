package com.krishe.govern.views.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.krishe.govern.networks.NetWorkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportsViewModel(application: Application) : AndroidViewModel(application), ReportsICallBack{
    // TODO: Implement the ViewModel
    lateinit var view: ReportsICallBack
    val data: MutableLiveData<List<ReportsItemModel>> = MutableLiveData<List<ReportsItemModel>>()

    fun onViewAvailable(v: ReportsICallBack) {
        view = v
    }

    fun getData(): LiveData<List<ReportsItemModel>> {
        return data
    }

    fun getReportsCall() {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            NetWorkCall.getMySavedImplements(this@ReportsViewModel)
        }
    }

    override fun onError(msg: String) {
        view.onPrHide()
        view.onError(msg)
    }

    override fun onSuccess(list: List<ReportsItemModel>) {
        view.onPrHide()
       // Log.e(ReportsFragment.TAG, "onSuccess: ${list[0].reportName}" )
        data.postValue(list)
        //view.onSuccess(list)
    }
    override fun onPrHide() {
    }

    override fun onPrShow() {
    }

    override fun onCleared() {
        super.onCleared()
    }
}