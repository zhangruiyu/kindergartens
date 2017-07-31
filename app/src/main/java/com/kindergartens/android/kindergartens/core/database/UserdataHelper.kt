package com.kindergartens.android.kindergartens.core.database

import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpHeaders
import com.raizlabs.android.dbflow.kotlinextensions.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

/**
 * Created by zhangruiyu on 2017/7/12.
 */
class UserdataHelper {
    //dbflow 会用主键作为标示 ,如果主键重复那么覆盖对应数据  扩展后会覆盖已经数据 新数据为空 那么会给数据库的也会覆盖为空
    companion object : AnkoLogger {
        private var tUser: TUserModel? = null
            set(value) {
                OkGo.getInstance().apply {
                    //在线就设置token
                    if (value?.isOnline ?: false) {
                        addCommonHeaders(HttpHeaders("token", value?.token))
                    }

                }
                field = value
            }

        fun getOnlineUser(): TUserModel? {
            //查询user
            if (tUser == null) {
                tUser = (select from TUserModel::class
                        where (TUserModel_Table.isOnline eq true))
                        .result
                debug { tUser }
            }
            if (tUser?.isOnline == false) {
                return null
            }
            return tUser
        }

        //从手机号获取用户 没有就新创建一个
        fun selectUserByTel(tel: String): TUserModel {
            //如果内存里的和要拿的一样
            if (tUser?.tel == tel) {
                return tUser!!
            }
            //从数据库取
            return (select from TUserModel::class
                    where (TUserModel_Table.tel eq tel))
                    .result ?: TUserModel()
        }

        //如果有用户执行闭包
        fun haveOnlineLet(block: (TUserModel) -> Unit) {
            val onlineUser = getOnlineUser()
            if (onlineUser != null) {
                block(onlineUser)
            }
        }

        //是否有在线用户
        fun haveOnlineUser(): Boolean {
            return getOnlineUser() != null
        }

        //退出登陆
        fun loginOut(block: () -> Unit = {}) {
            val onlineUser = getOnlineUser()
            onlineUser?.apply {
                isOnline = false
                token = ""
            }?.save()
            resetData()
            block.invoke()

        }

        fun resetData() {
            tUser = null
        }
    }
}