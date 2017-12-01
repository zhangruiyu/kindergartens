package com.kindergartens.android.kindergartens.core.ui

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

/**
 * Created by zhangruiyu on 16/11/4.
 */

open class ScrollChildSwipeRefreshLayout : SwipeRefreshLayout {

    private var mScrollUpChild: View? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun canChildScrollUp(): Boolean {
        if (mScrollUpChild != null) {
            return ViewCompat.canScrollVertically(mScrollUpChild, -1)
        }
        return super.canChildScrollUp()
    }

    fun setScrollUpChild(view: View) {
        mScrollUpChild = view
    }
}
