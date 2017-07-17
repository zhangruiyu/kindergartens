package com.kindergartens.android.kindergartens.core.modular.dynamic.data

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.dip

/**
 * Created by zhangruiyu on 2017/7/17.
 */

class SpaceItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView?, state: RecyclerView.State?) {
        val dip = view.context.dip(3)
        outRect.top = dip * 2
        outRect.left = dip
        outRect.right = dip

    }
}
