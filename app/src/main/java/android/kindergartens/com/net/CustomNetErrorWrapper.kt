package android.kindergartens.com.net

import android.content.Context
import android.kindergartens.com.KGApplication
import android.kindergartens.com.base.BaseActivity
import android.kindergartens.com.core.MainActivity
import android.kindergartens.com.ext.TSnackbarUtils
import com.apkfuns.logutils.LogUtils
import org.jetbrains.anko.toast

/**
 * Created by zhangruiyu on 2017/7/3.
 */
abstract class CustomNetErrorWrapper<T>(val context: Context? = null) : ErrorWrapper<T>() {

    override fun onError(e: Throwable) {
        super.onError(e)
        if (e is ApiException) {
            if (e.code == 1002) {
                MainActivity.instance?.homepageFragment3?.onVisible()
            }
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