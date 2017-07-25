package com.kindergartens.android.kindergartens.net

import com.apkfuns.logutils.LogUtils
import com.kindergartens.okrxkotlin.ErrorWrapper

/**
 * Created by zhangruiyu on 2017/7/3.
 */
abstract class CustomNetErrorWrapper<T> : ErrorWrapper<T>() {
    override fun onError(e: Throwable) {
        super.onError(e)
        LogUtils.e(e)
    }
}