package com.kindergartens.android.kindergartens.core.modular.dynamic.data

import com.kindergartens.android.kindergartens.base.BaseEntity
import java.io.File

/**
 * Created by zhangruiyu on 2017/7/16.
 */
class DynamicSelectedPics : BaseEntity {
    var url: File? = null
    var resourceId: Int? = null

    constructor()
    constructor(url: File) {
        this.url = url
    }

    constructor(resourceId: Int) {
        this.resourceId = resourceId
    }
}