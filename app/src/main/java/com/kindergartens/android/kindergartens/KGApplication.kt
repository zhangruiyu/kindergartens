package com.kindergartens.android.kindergartens

import android.app.Application
import android.os.Environment
import com.apkfuns.logutils.LogUtils
import com.kindergartens.android.kindergartens.core.ali.BizService
import com.kindergartens.okrxkotlin.OkRxInit
import com.mabeijianxi.smallvideorecord2.DeviceUtils
import com.mabeijianxi.smallvideorecord2.JianXiCamera
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
        Tools.init(this)
        FlowManager.init(FlowConfig.Builder(this).build())
        initNet()
        BizService.instance().init(this)
        initSmallVideo()
        LogUtils.getLogConfig().configTagPrefix(Tools.appTool().getPackageName(ctx))
                .configAllowLog(true)//是否开启
    }

    private fun initNet() {
        okInit = OkRxInit {
            context = this@KGApplication
            tag = Tools.appTool().getPackageName(ctx)
            headers = mapOf("os" to "android")
        }

    }

    fun initSmallVideo() {
        // 设置拍摄视频缓存路径
        val dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath("$dcim/mabeijianxi/")
            } else {
                JianXiCamera.setVideoCachePath(dcim.path.replace("/sdcard/",
                        "/sdcard-ext/") + "/mabeijianxi/")
            }
        } else {
            JianXiCamera.setVideoCachePath("$dcim/mabeijianxi/")
        }
        // 初始化拍摄，遇到问题可选择开启此标记，以方便生成日志
        JianXiCamera.initialize(false, null)
    }

    companion object {
        lateinit var kgApplication: KGApplication
        lateinit var okInit: OkHttpClient.Builder
    }
}