package com.kindergartens.android.kindergartens.core.modular.dynamic

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import ch.halcyon.squareprogressbar.SquareProgressBar
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.core.modular.dynamic.data.DynamicSelectedPic
import com.kindergartens.android.kindergartens.ext.width

/**
 * Created by zhangruiyu on 2017/7/22.
 */

class SelectedDynamicAdapter(val ctx: Context) : BaseQuickAdapter<DynamicSelectedPic, BaseViewHolder>(
        R.layout.layout_dynamic_selected_pics, listOf(DynamicSelectedPic(R.drawable.dynamic_selected_add))) {
    //    var mRequestOptions = RequestOptions().override(ctx.width() / 5, ctx.width() / 5).centerCrop()
    val width: Int = (ctx.width() / row - ctx.resources.getDimension(R.dimen.dynamic_select_pic_margin)).toInt()
    val params = ViewGroup.LayoutParams(width, width)
    val image_params = RelativeLayout.LayoutParams(width, width)

    override fun convert(helper: BaseViewHolder, item: DynamicSelectedPic) {
        val view = helper.getView<SquareProgressBar>(R.id.iv_selected_pic_one)
        view.setHoloColor(R.color.primary)
        view.width = 2
        view.layoutParams = params

        item.squareProgressBar = view
        item.position = helper.layoutPosition
        val layoutParams = helper.getView<View>(R.id.imageView1).layoutParams
        layoutParams.height = image_params.height
        layoutParams.width = image_params.width
        //最后一个 就是选择图片
        if (item.resourceId != null) {

//            helper.addOnClickListener(R.id.ll_selected_pic_one)
            Glide.with(ctx)
                    .load(item.resourceId)
                    .into(helper.getView<ImageView>(R.id.imageView1))
        } else {
            Glide.with(ctx)
                    .load(item.url).override(width, width).centerCrop()
                    .into(helper.getView<ImageView>(R.id.imageView1))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object {
        val row = 4
    }
}
