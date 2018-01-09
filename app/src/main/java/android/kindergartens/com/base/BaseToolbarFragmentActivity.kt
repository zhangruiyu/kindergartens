package android.kindergartens.com.base

import android.kindergartens.com.ext.yes
import android.support.v4.app.Fragment
import org.jetbrains.anko.AnkoLogger

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseToolbarFragmentActivity : BaseToolbarActivity(), AnkoLogger {
    protected var mFragments: ArrayList<Fragment>? = null
    protected var current_index: Int = 0


    override fun onPause() {
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

    override fun onResume() {
        super.onResume()
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