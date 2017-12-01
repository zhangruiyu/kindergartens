package android.kindergartens.com.core.modular.userinfo.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/8.
 */
class ProfileAlteredInfo(var code: Int,
                         var data: Data,
                         var msg: String) : BaseEntity {
    class Data(var checkGender: Int, var relationCheck: Int, var address: String, var avatarUrl: String)
}