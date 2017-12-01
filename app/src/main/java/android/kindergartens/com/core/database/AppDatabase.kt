package android.kindergartens.com.core.database

import android.kindergartens.com.core.database.AppDatabase.Companion.DatabaseName
import android.kindergartens.com.core.database.AppDatabase.Companion.VERSION
import com.raizlabs.android.dbflow.annotation.Database

/**
 * Created by zhangruiyu on 2017/7/12.
 */
@Database(name = DatabaseName, version = VERSION)
class AppDatabase {
    companion object {
        const val DatabaseName = "KGAppDatabase"
        const val VERSION = 1
    }
}