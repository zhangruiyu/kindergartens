package android.kindergartens.com.core.database

import android.kindergartens.com.core.modular.home.dummy.data.DynamicEntity
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.jetbrains.anko.custom.async
import com.raizlabs.android.dbflow.kotlinextensions.*

/**
 * Created by zhangruiyu on 2017/7/30.
 */
@Table(database = AppDatabase::class)
class TSchoolmateModel() {
    constructor(userId: String, nickName: String) : this() {
        this.userId = userId
        this.nickName = nickName
    }

    @PrimaryKey
    var userId: String? = null
    @Column
    var nickName: String? = null
}

class SchoolmateHelper {
    companion object {
        var sAllSchoolmate: MutableMap<String, String> = hashMapOf()
        fun saveSchoolmates(schoolmates: ArrayList<DynamicEntity.DynamicProfile>) {
            schoolmates.forEach {
                //异步保存
                TSchoolmateModel(it.userId, it.nickName).async().save()
            }
            sAllSchoolmate.clear()

        }

        fun saveSchoolmate(schoolmate: DynamicEntity.DynamicProfile) {
            //异步保存
            val tSchoolmateModel = TSchoolmateModel(schoolmate.userId, schoolmate.nickName)
            tSchoolmateModel.async().save()
            sAllSchoolmate.put(tSchoolmateModel.userId!!, schoolmate.nickName)
        }

        fun getALlSchoolmateAndRun(block: (MutableMap<String, String>) -> Unit) {
            if (sAllSchoolmate.isNotEmpty()) {
                block(sAllSchoolmate)
            } else {
                val list = (select from TSchoolmateModel::class
                        where (TSchoolmateModel_Table.nickName notEq "")).list
                list.forEach {
                    sAllSchoolmate.put(it.userId!!, it.nickName!!)
                }

                block(sAllSchoolmate)
            }
        }

        fun getNickName(id: String, block: (String) -> Unit) {
            getALlSchoolmateAndRun {
                if (it.containsKey(id)) {
                    block(it.getValue(id))
                } else {
                    block("已毕业同学")
                }
            }
        }
    }
}