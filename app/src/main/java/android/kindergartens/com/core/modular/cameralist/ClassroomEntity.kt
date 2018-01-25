package android.kindergartens.com.core.modular.cameralist

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/21.
 */
data class ClassroomEntity(var code: Int,
                           var msg: String,
                           var data: WrapperData) : BaseEntity {
    data class WrapperData(var data: List<Data>, var addition: String) : BaseEntity {
        data class Data(var childCount: Int,
                        var classroomImage: String,
                        var showName: String,
                        var isCorridor: Int,
                        var synopsis: String,
                        var unWatch: Int,
                        var kgCamera: KgCamera) : BaseEntity {
            data class KgCamera(var deviceName: String,
                                var deviceSerial: String,
                                var model: String,
                                var verifyCode: String, var isEncrypt: Int) : BaseEntity
        }
    }

}