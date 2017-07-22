package com.kindergartens.android.kindergartens.core.modular.dynamic

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import com.kindergartens.android.kindergartens.core.modular.dynamic.data.DynamicSelectedPic
import com.kindergartens.android.kindergartens.core.tools.cos.data.SignInfo
import com.kindergartens.android.kindergartens.ext.toText
import com.kindergartens.android.kindergartens.net.KGNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_edit_dynamic.*
import me.iwf.photopicker.PhotoPicker
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.io.File


class EditDynamicActivity : BaseToolbarActivity() {
    val select_pic = DynamicSelectedPic(R.drawable.dynamic_selected_add)
    var dynamic_type = PIC_TYPE //0是图片动态 1是视频动态
    lateinit var selectedAdapter: SelectedDynamicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndPermission.with(ctx).requestCode(200).permission(Manifest.permission.READ_EXTERNAL_STORAGE).callback(this).start()
        setContentView(R.layout.activity_edit_dynamic)

        init()
    }

    private fun init() {
        if (intent?.extras?.get(MediaRecorderActivity.VIDEO_URI) != null && intent?.extras?.get(MediaRecorderActivity.VIDEO_SCREENSHOT) != null) {
            dynamic_type = VIDEO_TYPE
            rl_video_info.visibility = View.VISIBLE
            Glide.with(ctx)
                    .load(File(intent.extras.getString(MediaRecorderActivity.VIDEO_SCREENSHOT)))
                    .into(iv_video_background)
        } else {
            selectedAdapter = SelectedDynamicAdapter(ctx)
            dynamic_type = PIC_TYPE
            rl_video_info.visibility = View.INVISIBLE
            rcv_pics.layoutManager = object : GridLayoutManager(ctx, SelectedDynamicAdapter.row) {}
            rcv_pics.adapter = selectedAdapter
//        rcv_pics.addItemDecoration(SpaceItemDecoration())
            selectedAdapter.setOnItemClickListener({ adapter, _, position ->

                if (position == adapter!!.itemCount - 1) {
                    //打开相册页面
                    startPickerActivity()
                } else {
                    toast("22")
                }
            })
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
                LogUtils.d(photos)
                selectedAdapter.setNewData(photos.map { DynamicSelectedPic(File(it)) } + select_pic)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dynamic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_send_dynamic) {
            val find = find<ActionMenuItemView>(R.id.menu_send_dynamic)
            if (edt_dynamic_content.toText().isEmpty()) {
                toast("发布内容不能为空")
                return true
            }
            ServerApi.getOCSPeriodEffectiveSignSign(0).subscribe(object : KGNetErrorWrapper<SignInfo>() {
                override fun onNext(t: SignInfo) {
                    LogUtils.d(t)
                    val list = selectedAdapter.data.subList(0, selectedAdapter.data.size - 1)
                    var count = 0
                    list.forEach {
                        it.block = {
                            count++
                            if (list.size == count) {
                                //全部完成了
                            }
                        }
                        //遍历然后上传图片
                        it.putPicForDynamicSelectedPic(t.sign, t.cosPath)
                    }
                }

            })
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        if (dynamic_type == PIC_TYPE)
            selectedAdapter.notifyDataSetChanged()
    }

    companion object {
        const val PIC_TYPE = 0
        const val VIDEO_TYPE = 1
    }
}
