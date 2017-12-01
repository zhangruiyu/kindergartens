package android.kindergartens.com.service

/**
 * Created by zhangruiyu on 2017/11/21.
 */
data class CheckUpdateEntity(var code: Int,
                             var msg: String,
                             var data: Data) {
    data class Data(var checkState: Int,
                    var message: String? = null,
                    var downloadUrl: String? = null)
}