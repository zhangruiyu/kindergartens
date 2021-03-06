package android.kindergartens.com

//import com.videogo.openapi.EZOpenSDK
import android.annotation.SuppressLint
import android.content.Context
import android.kindergartens.com.core.ali.BizService
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.browser.BrowserActivity
import android.kindergartens.com.ext.OkRxInit
import android.support.multidex.MultiDexApplication
import com.apkfuns.logutils.LogUtils
import com.mazouri.tools.Tools
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import com.scwang.smartrefresh.layout.header.BezierRadarHeader
import com.umeng.analytics.MobclickAgent
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import com.videogo.openapi.EZOpenSDK
import okhttp3.OkHttpClient
import org.jetbrains.anko.ctx


/**
 * Created by zhangruiyu on 2017/6/21.
 */
class KGApplication : MultiDexApplication() {
    init {
        PlatformConfig.setWeixin("wxc61b3dcd7a7b8d0b", "27b14020655a5df2f9881e2f79ea5c68")
        PlatformConfig.setQQZone("1105658225", "51KsgvmB9drZtkTh");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
    }
    override fun onCreate() {
        super.onCreate()
        kgApplication = this

        UMShareAPI.get(this)
        ZXingLibrary.initDisplayOpinion(this)
        FlowManager.init(FlowConfig.Builder(this).build())
        initNet()
        BizService.instance().init(this)
//        initSmallVideo()
        LogUtils.getLogConfig().configTagPrefix(Tools.appTool().getPackageName(ctx))
                .configAllowLog(true)//是否开启
        //查询出当前用户
        UserdataHelper.getOnlineUser()
        initEzOpen()

        Tools.init(this)
        val mPushAgent = PushAgent.getInstance(this)
//        mPushAgent.setDebugMode(Tools.apk().isAppDebug(this))
        mPushAgent.setDebugMode(true)
        //错误统计 debug模式下不开启
//        MobclickAgent.setCatchUncaughtExceptions(!Tools.apk().isAppDebug(this))
        //注册推送服务，每次调用register方法都会回调该接口

        mPushAgent.register(object : IUmengRegisterCallback {

            override fun onSuccess(deviceToken: String) {
                //注册成功会返回device token
                Constants.PushToken = deviceToken
            }

            override fun onFailure(s: String, s1: String) {

            }
        })

        mPushAgent.notificationClickHandler = object : UmengNotificationClickHandler() {
            override fun dealWithCustomAction(context: Context, msg: UMessage?) {
                if (msg != null && msg.extra.isNotEmpty()) {
                    //浏览器
                    if (msg.extra["type"]?.toInt() == 1) {
                        BrowserActivity.launch(context, msg.extra["url"]!!, msg.extra["title"]!!)
                    }
                } else {
                    super.dealWithCustomAction(context, msg)
                }
            }
        }
        MobclickAgent.enableEncrypt(!Tools.apk().isAppDebug(this))

    }

    private fun initEzOpen() {
        //如果萤石云报错 sdktarge设置为22 要不需要手动获取权限
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


    companion object {
        @SuppressLint("StaticFieldLeak")
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
                BallPulseFooter(context).setSpinnerStyle(SpinnerStyle.Translate)
            })
        }
    }
}
