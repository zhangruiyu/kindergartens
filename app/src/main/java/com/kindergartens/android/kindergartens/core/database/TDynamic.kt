package com.kindergartens.android.kindergartens.core.database

import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * Created by zhangruiyu on 2017/7/12.
 */
@Table(database = AppDatabase::class)
class TDynamic constructor() : BaseModel() {
    @PrimaryKey(autoincrement = true) var id: Int = 0
}