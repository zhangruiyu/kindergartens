package android.kindergartens.com.core.modular.auth.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/7/12.
 */

class LoginUserEntity(var data: Data) : BaseEntity {
    data class Data(var tel: String, var id: String,
                    var token: String, val gender: Int
                    , val address: String
                    , val schoolName: String
                    , val qqOpenId: String? = null
                    , val wxOpenId: String? = null
                    , val qqNickName: String? = null
                    , val wxNickName: String? = null
                    , val relation: Int = 0) : BaseEntity
}