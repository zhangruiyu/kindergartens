package android.kindergartens.com.core.modular.home.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/1.
 */
data class UserProfileEntity(var code: Int,
                             var data: Data,
                             var msg: String) : BaseEntity {
    data class Data(var classroomId: Int,
                    var nickName: String,
                    var schoolId: Int) : BaseEntity
}