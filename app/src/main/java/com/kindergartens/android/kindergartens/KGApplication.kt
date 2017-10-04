package com.kindergartens.android.kindergartens

//import com.videogo.openapi.EZOpenSDK
import android.app.Application
import android.os.Environment
import com.apkfuns.logutils.LogUtils
import com.kindergartens.android.kindergartens.core.ali.BizService
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.ext.OkRxInit
import com.mabeijianxi.smallvideorecord2.DeviceUtils
import com.mabeijianxi.smallvideorecord2.JianXiCamera
import com.mazouri.tools.Tools
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.BezierRadarHeader
import com.videogo.openapi.EZOpenSDK
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
//        initSmallVideo()
        LogUtils.getLogConfig().configTagPrefix(Tools.appTool().getPackageName(ctx))
                .configAllowLog(true)//是否开启
        //查询出当前用户
        UserdataHelper.getOnlineUser()
        initEzOpen()
    }

    private fun initEzOpen() {
        EZOpenSDK.showSDKLog(true)
        EZOpenSDK.enableP2P(false)
        EZOpenSDK.initLib(this, "b109fdee59b14b19b48927f627814c58", "")
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

        init {
            //static 代码段可以防止内存泄露
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreater { context, layout ->
                layout.setPrimaryColorsId(R.color.primary, android.R.color.white);//全局设置主题颜色
                BezierRadarHeader(context)
//                ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            };
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreater({ context, _ ->
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate)
            })
        }
    }
}
