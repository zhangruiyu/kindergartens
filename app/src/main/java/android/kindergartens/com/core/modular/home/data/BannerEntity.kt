package android.kindergartens.com.core.modular.home.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/1.
 */
data class BannerEntity(var code: Int,
                        var data: ArrayList<Data>,
                        var msg: String) : BaseEntity {
    data class Data(val title: String, val picUrl: String, val url: String) : BaseEntity
}