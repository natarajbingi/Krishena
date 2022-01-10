package com.krishe.govern

import android.app.Application
import com.krishe.govern.networks.NetWorkCall
/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class KrishApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetWorkCall.networkingSetup(applicationContext)
    }
}