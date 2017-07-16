package com.kindergartens.android.kindergartens.core.modular.dynamic

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import com.kindergartens.android.kindergartens.core.modular.dynamic.data.DynamicSelectedPics
import com.kindergartens.android.kindergartens.ext.width
import kotlinx.android.synthetic.main.activity_edit_dynamic.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast

class EditDynamicActivity : BaseToolbarActivity() {

    lateinit var selectedAdapter: SelectedAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dynamic)
        selectedAdapter = SelectedAdapter(ctx)
        init()
    }

    private fun init() {
        rcv_pics.layoutManager = object : GridLayoutManager(ctx, 5){}
        rcv_pics.adapter = selectedAdapter
//        添加跳转click
//        selectedAdapter.add
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dynamic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toast("item+${item.itemId}15313994094")
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        selectedAdapter.notifyDataSetChanged()
    }
}

class SelectedAdapter(val ctx: Context) : BaseQuickAdapter<DynamicSelectedPics, BaseViewHolder>(
        R.layout.layout_dynamic_selected_pics, listOf(DynamicSelectedPics("1", R.id.ll_selected_pic_one)
        , DynamicSelectedPics("1"), DynamicSelectedPics("1"), DynamicSelectedPics("1"), DynamicSelectedPics("1")
        , DynamicSelectedPics("1"), DynamicSelectedPics("1"))) {

    override fun convert(helper: BaseViewHolder, item: DynamicSelectedPics) {
        //最后一个 就是选择图片
        val params = ViewGroup.LayoutParams(ctx.width() / 5, ctx.width() / 5)
        if (item.resourceId != null) {
            helper.getView<LinearLayout>(R.id.ll_selected_pic_one).layoutParams = params
            Glide.with(ctx)
                    .load(item.resourceId)
                    .into(helper.getView<ImageView>(R.id.iv_selected_pic_one))
        } else {
            helper.getView<LinearLayout>(R.id.ll_selected_pic_one).layoutParams = params
            /*  Glide.with(ctx)
                      .load(R.drawable.dynamic_selected_add)
                      .into(helper.getView<ImageView>(R.id.iv_selected_pic_one))*/
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}