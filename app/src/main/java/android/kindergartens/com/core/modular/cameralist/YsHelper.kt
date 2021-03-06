package android.kindergartens.com.core.modular.cameralist

import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.ext.applyAndSave

/**
 * Created by zhangruiyu on 2017/8/18.
 */
class YsHelper {
    companion object {
        fun getLocalToken(): String? = UserdataHelper.getOnlineUser()?.ysToken
        fun saveYSToken(accessToken: String) {
            UserdataHelper.getOnlineUser()?.applyAndSave {
                ysToken = accessToken
            }
        }

    }
}