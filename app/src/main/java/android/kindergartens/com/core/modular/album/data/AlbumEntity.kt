package android.kindergartens.com.core.modular.album.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/11/16.
 */
data class AlbumEntity(var code: Int,
                       var msg: String,
                       var data: List<Data>): BaseEntity {
    data class Data(var data: String,
                    var addition: ArrayList<Addition>):BaseEntity {
        data class Addition(var picUrl: String,
                            var sequence: Int,
                            var createTime: String):BaseEntity
    }
}