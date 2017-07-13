package com.kindergartens.android.kindergartens.base

import android.support.v4.app.Fragment
import com.utils.kindergartens.yes
import org.jetbrains.anko.AnkoLogger

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseFragmentActivity : BaseActivity(), AnkoLogger {
    protected var mFragments: ArrayList<Fragment>? = null
    protected var current_index: Int = 0


    protected override fun onPause() {
        super.onPause()
        mFragments?.apply {
            (size > 0).yes {
                forEachIndexed { i, fragment ->
                    if (!fragment.isHidden && fragment.isAdded) {
                        supportFragmentManager.beginTransaction().hide(fragment).commit()
                        current_index = i
                    }
                }

            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        mFragments?.apply {
            (size > 0).yes {
                forEachIndexed { i, fragment ->
                    if (i == current_index) {
                        supportFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
                    }
                }

            }
        }


    }

    //添加fragment
    open fun add(fragment: Fragment) {
        if (mFragments == null)
            mFragments = ArrayList<Fragment>()
        mFragments!!.add(fragment)
    }
}