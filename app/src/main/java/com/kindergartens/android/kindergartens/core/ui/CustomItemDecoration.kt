package com.kindergartens.android.kindergartens.core.ui

import android.content.Context
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.ext.getColorSource
import com.yanyusong.y_divideritemdecoration.Y_Divider
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration
import org.jetbrains.anko.px2dip

/**
 * Created by zhangruiyu on 2017/8/12.
 */
class CustomItemDecoration(val ctx: Context) : Y_DividerItemDecoration(ctx) {
    override fun getDivider(itemPosition: Int): Y_Divider {
        val px2dip = ctx.px2dip(ctx.resources.getDimension(R.dimen.divider_height).toInt())
        return Y_DividerBuilder().setBottomSideLine(true, ctx.getColorSource(R.color.recycle_divider), px2dip, 0f, 0f).create()
    }

}