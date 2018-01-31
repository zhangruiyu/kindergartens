package android.kindergartens.com.core.modular.tels.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2018/1/31.
 */
data class TeacherEntity(var code: Int,
                         var msg: String,
                         var data: Data) : BaseEntity {
    data class Data(var teachers: List<Teachers>,
                    var students: List<Students>) : BaseEntity {
        data class Teachers(var realName: String,
                            var roleId: Int,
                            var showName: String,
                            var avatar: String) : BaseEntity

        data class Students(var realName: String,
                            var roleId: Int,
                            var avatar: String) : BaseEntity
    }
}