package android.kindergartens.com.core.modular.cameralist

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/18.
 */
data class YSToken(var code: Int,
                   var data: Data,
                   var msg: String) : BaseEntity {
    data class Data(var accessToken: String? = null) : BaseEntity
}