package com.kindergartens.android.kindergartens.base

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseActivity : RxAppCompatActivity() {
    override fun onResume() {
        super.onResume()
        runActivity = this
    }

    override fun onStop() {
        super.onStop()
//        runActivity = null
    }

    companion object {
        var runActivity: BaseActivity? = null
    }
}