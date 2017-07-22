package com.kindergartens.android.kindergartens.custom.ui

import android.view.ViewManager
import com.kindergartens.android.kindergartens.core.ui.BaseSwipeRefreshLayout
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView

/**
 * Created by zhangruiyu on 2017/6/21.
 */

fun ViewManager.baseSwipeRefreshLayout(theme: Int = 0) = baseSwipeRefreshLayout(theme) {}

inline fun ViewManager.baseSwipeRefreshLayout(theme: Int = 0, init: (@AnkoViewDslMarker BaseSwipeRefreshLayout).() -> Unit): BaseSwipeRefreshLayout {
    return ankoView({ BaseSwipeRefreshLayout(it) }, theme, init)
}

