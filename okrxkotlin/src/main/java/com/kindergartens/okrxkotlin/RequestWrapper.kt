package com.kindergartens.okrxkotlin

import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpMethod
import com.lzy.okrx2.adapter.ObservableBody
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by zhangruiyu on 2017/6/28.
 */
open class RequestWrapper<T> {
    var baseUrl: String = "http://192.168.43.20:8080"
    var url: String? = null
    var method: HttpMethod = HttpMethod.POST
    var params = mapOf<Any, Any>()
    var headers: Map<String, String>? = null
    @PublishedApi internal var _success: (T) -> Unit = {}
    @PublishedApi internal var _fail: (Throwable) -> Unit = {}
    @PublishedApi internal var _doOnTerminate: () -> Unit = {}
    @PublishedApi internal var _doStart: () -> Unit = {}
    fun onSuccess(onSuccess: (T) -> Unit) {
        _success = onSuccess
    }

    fun onFail(onFail: (Throwable) -> Unit) {
        _fail = onFail
    }

    fun doOnTerminate(doOnTerminate: () -> Unit) {
        _doOnTerminate = doOnTerminate
    }

    fun doStart(doStart: () -> Unit) {
        _doStart = doStart
    }

    companion object {
        val methods = listOf(HttpMethod.POST, HttpMethod.GET)
    }
}

inline fun <reified T> http(init: RequestWrapper<T>.() -> Unit) {
    val wrapper = RequestWrapper<T>()
    wrapper.init()
    executeForResult(wrapper)
}

inline fun <reified T> executeForResult(wrapper: RequestWrapper<T>) {
    if (wrapper.url == null) {
        throw RuntimeException("url必须不为空")
    }
    //判断类型
    val ok = if (RequestWrapper.methods.indexOf(wrapper.method) == 0) {
        OkGo.post<T>(wrapper.baseUrl + wrapper.url)
    } else OkGo.get<T>(wrapper.baseUrl + wrapper.url)

    val mutableParams = mutableMapOf<String, String>()
    for ((key, value) in wrapper.params) {
        mutableParams.put(key.toString(), value.toString())
    }
    //添加参数
    ok.params(mutableParams)
    //添加头部
    wrapper.headers?.forEach {
        ok.headers(it.key, it.value)
    }
    val observable = ok.converter(JsonConvert(T::class.java)).adapt(ObservableBody<T>())
    observable.subscribeOn(Schedulers.io()).doOnSubscribe {
        wrapper._doStart()
    }.doOnTerminate {
        wrapper._doOnTerminate()
    }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {

                override fun onSubscribe(d: Disposable) = Unit

                override fun onComplete() = Unit

                override fun onNext(t: T) {
                    wrapper._success(t)
                }

                override fun onError(e: Throwable) {
                    wrapper._fail(e)
                    e.printStackTrace()
//                    Log.e("http:::::", "", e)
                }

            })


}
