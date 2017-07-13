package com.kindergartens.android.kindergartens.base

import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseFragment : RxFragment(), AnkoLogger {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onVisible()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        debug { (if (hidden) "不可见" else "可见") + "======" + javaClass.canonicalName }
        try {
            if (!hidden) {
                //fragment显示时候调用
                onVisible()
            } else {
                onInVisible()
            }
        } catch (e: NullPointerException) {
            //有些fragment没有初始化就被调用影藏
            e.printStackTrace()
        }

    }


    open fun onVisible() {

    }

    open fun onInVisible() {

    }

}