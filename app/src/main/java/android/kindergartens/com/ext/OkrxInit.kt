package android.kindergartens.com.ext

import android.app.Application
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Created by zhangruiyu on 2017/7/1.
 */

open class OkRxInitWrapper {
    var tag: String = "OKRXHttp"
    var printLevel: com.lzy.okgo.interceptor.HttpLoggingInterceptor.Level = com.lzy.okgo.interceptor.HttpLoggingInterceptor.Level.BODY
    var colorLevel: Level = Level.WARNING
    var headers: Map<String, String>? = null
    lateinit var context: Application
}

inline fun OkRxInit(init: OkRxInitWrapper.() -> Unit): okhttp3.OkHttpClient.Builder {
    val wrapper = OkRxInitWrapper()
    wrapper.init()
    return initOkRx(wrapper)
}

fun initOkRx(wrapper: OkRxInitWrapper): okhttp3.OkHttpClient.Builder {
    return with(wrapper) {
        val builder = okhttp3.OkHttpClient.Builder()
        val loggingInterceptor = com.lzy.okgo.interceptor.HttpLoggingInterceptor(tag)
//log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(printLevel)
//log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(colorLevel)
        builder.addInterceptor(loggingInterceptor)
        //全局的读取超时时间
        builder.readTimeout(com.lzy.okgo.OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
        //全局的写入超时时间
        builder.writeTimeout(com.lzy.okgo.OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
        //全局的连接超时时间
        builder.connectTimeout(com.lzy.okgo.OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)

        val okRx = com.lzy.okgo.OkGo.getInstance().init(context)//必须调用初始化
                .setOkHttpClient(builder.build())               //必须设置OkHttpClient
                .setCacheMode(com.lzy.okgo.cache.CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(com.lzy.okgo.cache.CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        headers?.forEach {
            okRx.addCommonHeaders(com.lzy.okgo.model.HttpHeaders(it.key, it.value))
        }
        builder
    }

}