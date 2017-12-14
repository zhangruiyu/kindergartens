package android.kindergartens.com.core.modular.dynamic

import android.Manifest
import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.dynamic.data.DynamicSelectedPic
import android.kindergartens.com.core.modular.dynamic.data.VideoUpload
import android.kindergartens.com.core.modular.video.TCConstants
import android.kindergartens.com.core.modular.video.preview.TCVideoPreviewActivity
import android.kindergartens.com.core.tools.cos.data.SignInfo
import android.kindergartens.com.ext.getWaitDialog
import android.kindergartens.com.ext.safeDismiss
import android.kindergartens.com.ext.setUnCancel
import android.kindergartens.com.ext.toText
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_edit_dynamic.*
import me.iwf.photopicker.PhotoPicker
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.io.File


class EditDynamicActivity : BaseToolbarActivity() {
    private val select_pic = DynamicSelectedPic(R.drawable.dynamic_selected_add)
    var dynamic_type = PIC_TYPE //0是图片动态 1是视频动态
    val photos = ArrayList<String>()
    private lateinit var selectedAdapter: SelectedDynamicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndPermission.with(ctx).requestCode(200).permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA).callback(this).start()
        setContentView(R.layout.activity_edit_dynamic)

        init()
    }

    private fun init() {
        if (intent?.extras?.get(TCConstants.VIDEO_RECORD_VIDEPATH) != null && intent?.extras?.get(TCConstants.VIDEO_RECORD_COVERPATH) != null) {
            dynamic_type = VIDEO_TYPE
            rl_video_info.visibility = View.VISIBLE
            Glide.with(ctx)
                    .load(File(intent.extras.getString(TCConstants.VIDEO_RECORD_COVERPATH)))
                    .into(iv_video_background.find(R.id.imageView1))
            rl_video_info.setOnClickListener {
                val preViewIntent = Intent(ctx, TCVideoPreviewActivity::class.java).putExtras(intent.extras)
                startActivity(preViewIntent)
            }
        } else {
            selectedAdapter = SelectedDynamicAdapter(ctx)
            dynamic_type = PIC_TYPE
            rl_video_info.visibility = View.INVISIBLE
            rcv_pics.layoutManager = object : GridLayoutManager(ctx, SelectedDynamicAdapter.row) {}
            rcv_pics.adapter = selectedAdapter
            //        rcv_pics.addItemDecoration(SpaceItemDecoration())
            selectedAdapter.setOnItemClickListener({ adapter, _, position ->
                toast("22")
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
                .setPreviewEnabled(false).setSelected(photos)
                .start(this, PhotoPicker.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                photos.clear()
                photos.addAll(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS))
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
        if (item.itemId == R.id.menu_send) {
            val find = find<ActionMenuItemView>(R.id.menu_send)
            if (edt_dynamic_content.toText().isEmpty()) {
                toast("发布内容不能为空")
                return@onOptionsItemSelected true
            }
            val isVideoDynamic = dynamic_type == VIDEO_TYPE
            val dialog = getWaitDialog()
            dialog.setUnCancel()
            ServerApi.getOCSPeriodEffectiveSignSign(if (isVideoDynamic) {
                1
            } else {
                0
            }).subscribe(object : CustomNetErrorWrapper<SignInfo>() {
                override fun onNext(t: SignInfo) {
                    LogUtils.d(t)
                    //是视频
                    if (dynamic_type == VIDEO_TYPE) {
                        uploadVideoDynamics(t.data, dialog)
                    } else {
                        //图片
                        uploadPicDynamics(t.data, dialog)
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    dialog.safeDismiss()
                }

            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadVideoDynamics(t: SignInfo.Data, dialog: MaterialDialog?) {
        VideoUpload(intent?.extras?.get(TCConstants.VIDEO_RECORD_COVERPATH) as String, intent?.extras?.get(TCConstants.VIDEO_RECORD_VIDEPATH) as String
                , {
            if (it.isSucceed) {
                //成功后回调
                ServerApi.commitDynamicVideo(edt_dynamic_content.toText(), it.screenshot_server_url, it.video_server_url, it.video_long)
                        .doOnTerminate { dialog.safeDismiss() }.subscribe(object : CustomNetErrorWrapper<Any>() {
                    override fun onNext(any: Any) {
                        toast("消息发布成功!")
                        dialog.safeDismiss()
                        finish()
                    }

                })
            } else {
                //失败
                dialog.safeDismiss()
            }

        })
                .putPicForDynamicSelectedPic(t.sign, t.cosPath)
    }

    fun uploadPicDynamics(t: SignInfo.Data, dialog: MaterialDialog?) {
        val uploadPics = ArrayList<DynamicSelectedPic.PicOrderInfo>()
        val list = selectedAdapter.data.subList(0, selectedAdapter.data.size - 1)
        list.forEach {
            //遍历然后上传图片
            it.putPicForDynamicSelectedPic(t.sign, t.cosPath, uploadPics, list.size, {
                if (it.isSucceed) {
                    //图片上传完毕 开始把信息给服务端
                    ServerApi.commitDynamicPic(edt_dynamic_content.toText(), it.uploadPics!!)
                            .doOnTerminate { dialog?.safeDismiss() }.subscribe(object : CustomNetErrorWrapper<Any>() {
                        override fun onNext(t: Any) {
                            toast("消息发布成功!")
                            finish()
                        }
                    })
                } else {
                    //失败
                    dialog?.safeDismiss()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (dynamic_type == PIC_TYPE)
            selectedAdapter.notifyDataSetChanged()
    }

    companion object {
        const val PIC_TYPE = 0
        const val VIDEO_TYPE = 1
        /* const val VIDEO_URI = "VIDEO_URI"
         const val VIDEO_SCREENSHOT = "VIDEO_SCREENSHOT"
         const val VIDEO_DURATION = "VIDEO_DURATION"
         fun startVideoDynamicActivity(context: Context, videoPath: String, videoImage: String, duration: String) {
             context.startActivity<EditDynamicActivity>(VIDEO_URI to videoPath, VIDEO_SCREENSHOT to videoImage, VIDEO_DURATION to duration)
         }*/
    }
}
