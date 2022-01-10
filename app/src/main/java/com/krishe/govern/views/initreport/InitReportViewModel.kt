package com.krishe.govern.views.initreport

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krishe.govern.utils.BaseViewModel
import com.krishe.govern.utils.KrishEvent

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class InitReportViewModel(application: Application) : BaseViewModel(application) {

    private val _scanningProgress = MutableLiveData<KrishEvent<Int>>()
    val scanningProgress: LiveData<KrishEvent<Int>> = _scanningProgress


    fun setScanningProgress(value: Int) {
        _scanningProgress.value = KrishEvent(value)
    }

}