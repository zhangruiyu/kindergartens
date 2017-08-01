package com.kindergartens.android.kindergartens.net

import android.content.Context
import com.apkfuns.logutils.LogUtils
import com.kindergartens.android.kindergartens.KGApplication
import com.kindergartens.okrxkotlin.ErrorWrapper
import org.jetbrains.anko.toast

/**
 * Created by zhangruiyu on 2017/7/3.
 */
abstract class CustomNetErrorWrapper<T>(val context: Context? = null) : ErrorWrapper<T>() {

    override fun onError(e: Throwable) {
        super.onError(e)
        if (e is IllegalStateException) {
            KGApplication.kgApplication.toast(e.localizedMessage!!)
        }

        LogUtils.e(e)
    }
}