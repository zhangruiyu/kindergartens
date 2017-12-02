package android.kindergartens.com.core.database

import android.kindergartens.com.base.BaseEntity
import android.kindergartens.com.core.database.AppDatabase
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by zhangruiyu on 2017/7/12.
 */
@Table(database = AppDatabase::class)
class TDynamic constructor() : BaseModel(), BaseEntity {
    @PrimaryKey(autoincrement = true) var id: Int = 0
}