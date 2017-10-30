package com.kindergartens.android.kindergartens.core.modular.classroom.data

import com.kindergartens.android.kindergartens.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/21.
 */
data class ClassroomEntity(var code: Int,
                           var msg: String,
                           var data: List<Data>) : BaseEntity {
    data class Data(var childCount: Int,
                    var classroomImage: String,
                    var showName: String,
                    var isCorridor: Int,
                    var kgCamera: KgCamera) : BaseEntity {
        data class KgCamera(var deviceName: String,
                            var deviceSerial: String,
                            var model: String,
                            var verifyCode: String, var isEncrypt: Int) : BaseEntity
    }
}