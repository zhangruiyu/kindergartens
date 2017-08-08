package com.kindergartens.okrxkotlin


import com.lzy.okgo.convert.Converter
import okhttp3.Response


/**
 * Created by zhangruiyu on 2017/6/30.
 */
open class JsonConvert<T> : Converter<T> {

    private var clazz: Class<T>? = null

    constructor() {}


    constructor(clazz: Class<T>) {
        this.clazz = clazz
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象，生成onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T {

        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        // 如果你对这里的代码原理不清楚，可以看这里的详细原理说明: https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
//        val body = response.body()
//        val jsonReader = JsonReader(body!!.charStream())
        if (clazz != null) {
            val string = response.body()!!.string()
            // 泛型格式如下： new JsonCallback<LzyResponse<内层JavaBean>>(this)
            val lzyResponse = Convert.fromJson(string, LzyResponse::class.java)
            response.close()
            val code = lzyResponse.code
            //这里的0是以下意思
            //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
            if (code == 200) {
//                val json = JsonSplit.split(string)
                @Suppress("UNCHECKED_CAST")
                return Convert.fromJson(string, clazz!!)
            } else {
                //直接将服务端的错误信息抛出，onError中可以获取
                throw ApiException(code, lzyResponse.msg)
            }

        } else {
            throw IllegalStateException("需要传入泛型")
        }

    }

}