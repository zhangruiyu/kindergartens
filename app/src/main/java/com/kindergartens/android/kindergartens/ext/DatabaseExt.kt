package com.kindergartens.android.kindergartens.ext

import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by zhangruiyu on 2017/7/13.
 */

//执行完闭包内容后保存
inline fun <T : BaseModel> T.applyAndSave(block: T.() -> Unit): T {
    block()
    save()
    return this
}