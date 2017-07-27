package com.kindergartens.android.kindergartens.core.ui

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.kindergartens.android.kindergartens.R

/**
 * Created by zhangruiyu on 2017/7/27.
 */
class CustomLoadMoreView: LoadMoreView() {

    override fun getLayoutId(): Int {
        return R.layout.layout_quick_view_load_more
    }

    override fun getLoadingViewId(): Int {
        return R.id.load_more_loading_view
    }

    override fun getLoadFailViewId(): Int {
        return R.id.load_more_load_fail_view
    }

    override fun getLoadEndViewId(): Int {
        return R.id.load_more_load_end_view
    }
}