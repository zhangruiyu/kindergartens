package android.kindergartens.com.core.modular.auth.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/7/12.
 */

class CodeWrapperEntity(var data: Data): BaseEntity {
    data class Data(var data: String):BaseEntity
}