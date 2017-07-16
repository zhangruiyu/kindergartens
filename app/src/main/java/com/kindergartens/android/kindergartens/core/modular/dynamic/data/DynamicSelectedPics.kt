package com.kindergartens.android.kindergartens.core.modular.dynamic.data

import com.kindergartens.android.kindergartens.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/7/16.
 */
class DynamicSelectedPics(val url: String, val resourceId: Int?) : BaseEntity {
    constructor(url: String) : this(url, null)
}