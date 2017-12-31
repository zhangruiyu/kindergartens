package android.kindergartens.com.core.modular.home.data

import android.kindergartens.com.base.BaseEntity

/**
 * Created by zhangruiyu on 2017/8/1.
 */
data class SchoolEntity(var code: Int,
                        var data: SchoolInfo,
                        var msg: String) : BaseEntity {


    data class SchoolInfo(val id: Int, var address: String, val tel: String, val schoolName: String, val shcoolPicture: String) : BaseEntity

}