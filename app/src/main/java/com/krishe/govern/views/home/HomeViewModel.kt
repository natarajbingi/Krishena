package com.krishe.govern.views.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.krishe.govern.models.Data
import com.krishe.govern.models.ImplementsDataRes
import com.krishe.govern.networks.NetWorkCall
import com.krishe.govern.utils.BaseViewModel
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.utils.Sessions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeViewModel(application: Application) : BaseViewModel(application), InitIReportCallBackReturn {

    lateinit var view: InitIReportCallBack
    lateinit var sessions: Sessions

    override fun onCleared() {
        super.onCleared()
    }

    fun onViewAvailable(v: InitIReportCallBack,sessions: Sessions) {
        view = v
        this.sessions = sessions
    }

    fun onGetImplementList() {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            NetWorkCall.getDefaultImplement(this@HomeViewModel)
        }
    }

    fun getStatisticImplement(paramJson: JSONObject) {
        view.onPrShow()
        viewModelScope.launch(Dispatchers.IO) {
            NetWorkCall.getStatisticImplement(this@HomeViewModel, paramJson)
        }
    }

    override fun onError(msg: String) {
        view.onPrHide()
    }

    override fun onSuccessImplementList(res: ImplementsDataRes) {
        view.onPrHide()
        sessions.setUserObj(res, KrisheUtils.implementListSession)
        sessions.setUserString(KrisheUtils.dateTime("nope"), KrisheUtils.oldProcessingDate)
        setImplementList(res.data)
       // Log.e("TAG", "onSuccessImplementList: "+list)
    }

    override fun onSuccessStatistics(msg: String) {
        view.onPrHide()
        view.onSuccessStatistics(msg)
    }

    fun setImplementList(data: List<Data>) {
        _data.postValue(data)
    }

}