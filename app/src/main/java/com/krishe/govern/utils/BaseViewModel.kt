package com.krishe.govern.utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krishe.govern.models.Data

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    var _data: MutableLiveData<List<Data>> = MutableLiveData()
    var data: LiveData<List<Data>> = _data
}