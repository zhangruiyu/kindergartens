package com.kindergartens.okrxkotlin

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by zhangruiyu on 2017/7/3.
 */
open class NetErrorWrapper<T> : Observer<T> {
    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

    override fun onComplete() = Unit

    override fun onNext(t: T) = Unit

    override fun onSubscribe(d: Disposable) = Unit
}