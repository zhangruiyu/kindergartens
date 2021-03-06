package android.kindergartens.com.core.database

import android.kindergartens.com.base.BaseEntity
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by zhangruiyu on 2017/7/12.
 */

@Table(database = AppDatabase::class)
class TUserModel : BaseModel(), BaseEntity {
    @PrimaryKey
    var tel: String? = null
    @Column
    var id: String? = null
    @Column(defaultValue = "")
    var token: String? = null
    @Column
    var isOnline: Boolean = false
    @Column
    var nickName: String? = null
    @Column
    var roleCode: String? = null
    @Column
    var avatar: String? = null
        get() {
            return if (field?.isEmpty() != false) {
                "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3063611797,2186093747&fm=117&gp=0.jpg"
            } else {
                field
            }
        }
        set(value) {
            if (value?.isEmpty() != false) {
                return
            } else {
                field = value
            }
        }
    @Column
    var gender: Int? = null
    @Column
    var address: String? = null
    @Column(defaultValue = "0")
    var relation: Int? = null
    @Column
    var ysToken: String? = null
    @Column
    var schoolName: String? = null
    @Column
    var qqOpenId: String? = null
    @Column
    var wxOpenId: String? = null
    @Column
    var qqNickName: String? = null
    @Column
    var wxNickName: String? = null
}

inline fun TUser(init: TUserModel.() -> Unit): TUserModel {
    val wrapper = TUserModel()
    wrapper.init()
    wrapper.save()
    return wrapper
}
