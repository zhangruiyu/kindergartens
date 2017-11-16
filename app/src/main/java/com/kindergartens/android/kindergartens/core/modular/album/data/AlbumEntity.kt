package com.kindergartens.android.kindergartens.core.modular.album.data

/**
 * Created by zhangruiyu on 2017/11/16.
 */
data class AlbumEntity(var code: Int,
                       var msg: String,
                       var data: List<Data>) {
    data class Data(var data: String,
                    var addition: ArrayList<Addition>) {
        data class Addition(var picUrl: String,
                            var sequence: Int,
                            var createTime: String)
    }
}