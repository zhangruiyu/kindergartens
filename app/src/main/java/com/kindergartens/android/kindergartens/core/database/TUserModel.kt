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
    @Column var nickName: String? = null
    @Column var avatar: String? = null
        get() {
            if (field?.isEmpty() ?: true) {
                return "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3063611797,2186093747&fm=117&gp=0.jpg"
            } else {
                return field
            }
        }
}

inline fun TUser(init: TUserModel.() -> Unit): TUserModel {
    val wrapper = TUserModel()
    wrapper.init()
    wrapper.save()
    return wrapper
}
