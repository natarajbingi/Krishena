package com.krishe.govern

import android.app.Application
import com.krishe.govern.networks.NetWorkCall

class KrishApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetWorkCall.networkingSetup(applicationContext)
    }
}