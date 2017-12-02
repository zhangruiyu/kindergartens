package android.kindergartens.com.core.modular.eat.data

import android.kindergartens.com.base.BaseEntity
import java.util.*

/**
 * Created by zhangruiyu on 2017/10/4.
 */
data class EatEntity(var code: Int,
                     var msg: String,
                     var data: List<Data>): BaseEntity {
    data class Data(var breakfast: String,
                    var lunch: String,
                    var supper: String,
                    var createTime: Date):BaseEntity
}