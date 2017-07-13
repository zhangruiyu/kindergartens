package com.kindergartens.android.kindergartens.core.modular.auth.data

/**
 * Created by zhangruiyu on 2017/7/12.
 */
data class asdf(var code: Int,
                var data: Data,
                var msg: String) {
    data class Data(
                    var tel: String,
                    var token: String) {
    }
}