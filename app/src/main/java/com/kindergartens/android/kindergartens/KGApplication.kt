package com.kindergartens.android.kindergartens

import android.app.Application
import com.kindergartens.okrxkotlin.OkRxInit
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager

/**
 * Created by zhangruiyu on 2017/6/21.
 */
class KGApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        kgApplication = this
        FlowManager.init(FlowConfig.Builder(this).build())
        initNet()
    }

    private fun initNet() {
        OkRxInit {
            context = this@KGApplication
            tag = "幼儿园小助手LOG"
            headers = mapOf("os" to "android","token" to "adb")
        }

    }

    companion object {
        lateinit var kgApplication: KGApplication
    }
}