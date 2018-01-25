package android.kindergartens.com.core.modular.cameralplay

/**
 * Created by zhangruiyu on 2018/1/25.
 */
data class EZPlayURLParams constructor(var deviceSerial: String = "",
                                       var cameraNo: Int = 0,
                                       var videoLevel: Int = 1,
                                       var verifyCode: String? = null) {
    constructor() : this("", 0, 1)
}