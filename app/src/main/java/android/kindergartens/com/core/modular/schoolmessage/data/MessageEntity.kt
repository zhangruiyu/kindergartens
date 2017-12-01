package android.kindergartens.com.core.modular.schoolmessage.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/9.
 */
data class MessageEntity(var code: Int,
                         var data: ArrayList<Data>,
                         var msg: String) : BaseEntity {
    data class Data(var message: String,
                    var createTime: String) : BaseEntity
}