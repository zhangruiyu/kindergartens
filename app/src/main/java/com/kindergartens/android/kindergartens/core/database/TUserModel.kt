package com.kindergartens.android.kindergartens.core.database

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by zhangruiyu on 2017/7/12.
 */

@Table(database = AppDatabase::class)
class TUserModel : BaseModel() {
    @PrimaryKey var tel: String? = null
    @Column var id: String? = null
    @Column(defaultValue = "") var token: String? = null
    @Column var isOnline: Boolean = false
}

inline fun TUser(init: TUserModel.() -> Unit): TUserModel {
    val wrapper = TUserModel()
    wrapper.init()
    wrapper.save()
    return wrapper
}
