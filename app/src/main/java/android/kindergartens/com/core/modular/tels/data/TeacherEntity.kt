package android.kindergartens.com.core.modular.tels.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/10/4.
 */
data class TeacherEntity(var code: Int,
                         var msg: String,
                         var data: List<Data>) : BaseEntity {
    data class Data(var roleId: String,
                    var realName: String,
                    var avatar: String
    ) : BaseEntity
}