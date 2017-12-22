package android.kindergartens.com.core.modular.home.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/1.
 */
data class BannerEntity(var code: Int,
                        var data: WrapperInfo,
                        var msg: String) : BaseEntity {
    data class WrapperInfo(val data: ArrayList<Data>, val addition: Int) {
        data class Data(val title: String, val picUrl: String, val url: String) : BaseEntity
    }

}