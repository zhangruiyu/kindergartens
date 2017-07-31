package com.kindergartens.android.kindergartens.core.modular.home.dummy

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.core.database.SchoolmateHelper
import com.kindergartens.android.kindergartens.core.modular.home.dummy.data.DynamicEntity
import com.kindergartens.android.kindergartens.core.tools.TimeUtil
import com.kindergartens.android.kindergartens.ext.width
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.jetbrains.anko.dimen


/**
 * Created by zhangruiyu on 2017/7/26.
 */
class DynamicAdapter(val ctx: Context, val childClick: (DynamicAdapter, View, Int) -> Unit) : BaseQuickAdapter<DynamicEntity.Data, BaseViewHolder>(R.layout.layout_item_dynamic) {

    override fun convert(helper: BaseViewHolder, item: DynamicEntity.Data) {
        Glide.with(ctx).load(R.drawable.ic_face_primary_24dp).bitmapTransform(CropCircleTransformation(ctx)).into(helper.getView<ImageView>(R.id.iv_dynamic_head_pic))
        helper.setText(R.id.tv_dynamic_create_time, TimeUtil.getTimeFormatText(item.createTime))
                .setText(R.id.tv_dynamic_content, item.content)
                .setText(R.id.tv_dynamic_nick_name, item.nickName).addOnClickListener(R.id.iv_reply).addOnClickListener(R.id.iv_share).addOnClickListener(R.id.iv_liked)
                .setTag(R.id.iv_reply, item.id)
        helper.getView<View>(R.id.iv_liked).isFocusable = true
        //设置图片start
        val recyclerView = helper.getView<RecyclerView>(R.id.rcy_dynamic_pic)
        val layoutParams = recyclerView.layoutParams
        var row: Int
        if (item.tails.kgDynamicPics.size % 3 != 0) {
            row = item.tails.kgDynamicPics.size / 3 + 1
        } else {
            row = item.tails.kgDynamicPics.size / 3
        }
        val item_width = ctx.width() - ctx.dimen(R.dimen.item_normal_margin) * 2
        layoutParams.width = item_width
        LogUtils.e("动态图片row == $row")
        layoutParams.height = item_width / 3 * row
        val layoutManager = object : GridLayoutManager(ctx, if (item.tails.kgDynamicPics.size > 2) 3 else item.tails.kgDynamicPics.size) {}
        layoutManager.isAutoMeasureEnabled = true
        recyclerView.layoutManager = layoutManager
//        rcv_pics.addItemDecoration(SpaceItemDecoration())

        if (recyclerView.adapter == null) {
            recyclerView.adapter = DynamicPicAdapter(ctx)
        }

        val adapter = recyclerView.adapter
        if (adapter is DynamicPicAdapter) {
            adapter.openLoadAnimation()
            adapter.setNewData(item.tails.kgDynamicPics)
            adapter.setOnItemClickListener({ adapter, _, position ->

                if (position == adapter!!.itemCount - 1) {
                    //打开相册页面
//                    startPickerActivity()
                } else {
//                    toast("22")
                }
            })
        }
        //设置图片end

        //设置评论start
        val commentLinearLayout = helper.getView<LinearLayout>(R.id.ll_dynamic_comment)
        commentLinearLayout.removeAllViews()
        val groupBy = item.tails.kgDynamicComment.groupBy({
            //按照tag排序呢
            it.groupTag
        })
        LogUtils.e("分组后的集合有${groupBy.size}个数")
        groupBy.forEach { comments ->
            comments.value.forEachIndexed { index, kgDynamicComment ->
                val textView = TextView(ctx)
                if (index != 0) {
                    textView.setPadding(120, 0, 0, 0)
                } else {
                    textView.setPadding(0, 0, 0, 0)
                }
                SchoolmateHelper.getNickName(kgDynamicComment.userId, {
                    nickName ->
                    val style = SpannableStringBuilder("$nickName:${kgDynamicComment.commentContent}")
                    style.setSpan(ForegroundColorSpan(Color.GRAY), 0, nickName.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    style.setSpan(ForegroundColorSpan(Color.BLACK), nickName.length, style.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    textView.text = style
                })
                textView.tag = kgDynamicComment
                commentLinearLayout.addView(textView)
                textView.setOnClickListener { childClick(this@DynamicAdapter, textView, helper.layoutPosition - headerLayoutCount) }
            }
        }
    }


    //设置评论end
}


class DynamicPicAdapter(val ctx: Context) : BaseQuickAdapter<DynamicEntity.Data.Tails.KgDynamicPics, BaseViewHolder>(R.layout.layout_item_dynamic_pic) {
    override fun convert(helper: BaseViewHolder, item: DynamicEntity.Data.Tails.KgDynamicPics) {
        val imageView = helper.getView<ImageView>(R.id.iv_dynamic_item_pic)
        Glide.with(ctx).load(item.picUrl).into(imageView)
        val layoutParams = imageView.layoutParams
        layoutParams.width = ctx.width() / 3
        layoutParams.height = ctx.width() / 3
    }

}
