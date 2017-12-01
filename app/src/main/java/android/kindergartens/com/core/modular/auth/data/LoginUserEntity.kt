package android.kindergartens.com.core.modular.auth.data

/**
 * Created by zhangruiyu on 2017/7/12.
 */

class LoginUserEntity(var data: Data) {
    data class Data(var tel: String, var id: String,
                    var token: String, val gender: Int
                    , val address: String
                    , val relation: Int = 0)
}