package com.kindergartens.android.kindergartens.base

import android.app.Activity
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import java.util.*

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseActivity : RxAppCompatActivity() {
    override fun onResume() {
        super.onResume()
        runActivity = this
        activists.add(this)
    }

    override fun onStop() {
        super.onStop()
//        runActivity = null
        activists.remove(this)
    }

    companion object {
        @JvmStatic
        var runActivity: Activity? = null
        @JvmStatic
        var activists: LinkedList<Activity> = LinkedList()

        @JvmStatic
        fun exitApp() {
            for (activist in activists) {
                activist.finish()
            }
            activists.clear()
        }
    }
}