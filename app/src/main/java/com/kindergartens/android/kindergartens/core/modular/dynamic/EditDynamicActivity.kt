package com.kindergartens.android.kindergartens.core.modular.dynamic

import android.Manifest
import android.content.Context
import android.content.Intent
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
import com.kindergartens.android.kindergartens.core.modular.dynamic.data.SpaceItemDecoration
import com.kindergartens.android.kindergartens.ext.width
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_edit_dynamic.*
import me.iwf.photopicker.PhotoPicker
import org.jetbrains.anko.ctx
import org.jetbrains.anko.debug
import org.jetbrains.anko.toast
import java.io.File


class EditDynamicActivity : BaseToolbarActivity() {
    val select_pic = DynamicSelectedPics(R.drawable.dynamic_selected_add)
    lateinit var selectedAdapter: SelectedAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndPermission.with(ctx).requestCode(200).permission(Manifest.permission.READ_EXTERNAL_STORAGE).callback(this).start()
        setContentView(R.layout.activity_edit_dynamic)
        selectedAdapter = SelectedAdapter(ctx)


        init()
    }

    private fun init() {
        rcv_pics.layoutManager = object : GridLayoutManager(ctx, SelectedAdapter.row) {}
        rcv_pics.adapter = selectedAdapter
        rcv_pics.addItemDecoration(SpaceItemDecoration())
        selectedAdapter.setOnItemClickListener { adapter, view, position ->

            if (position == adapter!!.itemCount - 1) {
                startPickerActivity()
            } else {
                toast("22")
            }
        }
    }

    @PermissionYes(200)
    fun startPickerActivity() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                val photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
                debug { photos }
                selectedAdapter.setNewData(photos.map { DynamicSelectedPics(File(it)) } + select_pic)
            }
        }

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
        R.layout.layout_dynamic_selected_pics, listOf(DynamicSelectedPics(R.drawable.dynamic_selected_add))) {
    //    var mRequestOptions = RequestOptions().override(ctx.width() / 5, ctx.width() / 5).centerCrop()
    val width: Int = (ctx.width() / row - ctx.resources.getDimension(R.dimen.dynamic_select_pic_margin)).toInt()
    val params = ViewGroup.LayoutParams(width, width)

    override fun convert(helper: BaseViewHolder, item: DynamicSelectedPics) {
        //最后一个 就是选择图片

        if (item.resourceId != null) {
            helper.getView<LinearLayout>(R.id.ll_selected_pic_one).layoutParams = params
//            helper.addOnClickListener(R.id.ll_selected_pic_one)
            Glide.with(ctx)
                    .load(item.resourceId)
                    .into(helper.getView<ImageView>(R.id.iv_selected_pic_one))
        } else {
            helper.getView<LinearLayout>(R.id.ll_selected_pic_one).layoutParams = params
            Glide.with(ctx)
                    .load(item.url).override(width, width).centerCrop()
                    .into(helper.getView<ImageView>(R.id.iv_selected_pic_one))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
    companion object {
        val row = 4
    }
}
