package com.kindergartens.android.kindergartens.net

import android.content.Context
import com.apkfuns.logutils.LogUtils
import com.kindergartens.android.kindergartens.KGApplication
import com.kindergartens.android.kindergartens.base.BaseActivity
import com.kindergartens.android.kindergartens.ext.TSnackbarUtils
import org.jetbrains.anko.toast

/**
 * Created by zhangruiyu on 2017/7/3.
 */
abstract class CustomNetErrorWrapper<T>(val context: Context? = null) : ErrorWrapper<T>() {

    override fun onError(e: Throwable) {
        super.onError(e)
        if (e is ApiException) {
            if (BaseActivity.runActivity != null) {
                TSnackbarUtils.toFail(BaseActivity.runActivity!!, e.errorMessage).show()
                return@onError
            }
        }
        KGApplication.kgApplication.toast(e.localizedMessage!!)
        LogUtils.e(e)
    }

    fun filterCustomException(e: Throwable, apiException: (ApiException) -> Unit, vararg filterCodes: Int) {
        if (e is ApiException) {
            val asList = filterCodes.asList()
            if (asList.contains(e.code)) {
                apiException(e)
                return@filterCustomException
            }
        }
        onError(e)
    }
}