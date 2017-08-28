package com.kindergartens.android.kindergartens.core.modular.classroom.data

import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.ext.applyAndSave

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