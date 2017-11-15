package com.kindergartens.android.kindergartens.core.modular.home

import android.content.Context
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.core.modular.home.data.HomepageItemBean

/**
 * Created by zhangruiyu on 2017/6/22.
 */
class HomepageAdapter(val context: Context) : BaseQuickAdapter<HomepageItemBean, BaseViewHolder>(R.layout.layout_item_homepage) {
    override fun convert(helper: BaseViewHolder, item: HomepageItemBean) {
        helper.setText(R.id.tv_text, item.title)
        Glide.with(context).load(item.iconId).into(helper.getView(R.id.aiv_homepage_item_icon))
    }

}