package com.kindergartens.android.kindergartens

import android.app.Application
import com.kindergartens.okrxkotlin.OkRxInit
import com.mazouri.tools.Tools
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import okhttp3.OkHttpClient
import org.jetbrains.anko.ctx

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
        okinit = OkRxInit {
            context = this@KGApplication
            tag = Tools.appTool().getPackageName(ctx)
            headers = mapOf("os" to "android","token" to "adb")
        }

    }

    companion object {
        lateinit var kgApplication: KGApplication
        lateinit var okinit: OkHttpClient.Builder
    }
}