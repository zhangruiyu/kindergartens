package com.kindergartens.android.kindergartens.core.ui

import android.content.Context
import android.util.AttributeSet
import com.kindergartens.android.kindergartens.R

/**
 * Created by zhangruiyu on 2017/1/11.
 */

class BaseSwipeRefreshLayout : ScrollChildSwipeRefreshLayout {
    constructor(context: Context) : super(context) {
        initColor()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initColor()
    }


    private fun initColor() {
        setColorSchemeResources(R.color.accent,R.color.red_dark,R.color.green_light)
    }
}
