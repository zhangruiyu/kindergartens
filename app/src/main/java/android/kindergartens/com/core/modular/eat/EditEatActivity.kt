package android.kindergartens.com.core.modular.eat

import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.dynamic.data.DynamicSelectedPic
import android.kindergartens.com.core.tools.cos.data.SignInfo
import android.kindergartens.com.core.ui.CustomSquareProgressBar
import android.kindergartens.com.ext.getWaitDialog
import android.kindergartens.com.ext.safeDismiss
import android.kindergartens.com.ext.toText
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mazouri.tools.Tools
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_edit_eat.*
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.PhotoPreview
import org.jetbrains.anko.*
import java.io.File

class EditEatActivity : BaseToolbarActivity() {
    private val photos: ArrayList<DynamicSelectedPic> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_eat)

        iv_edit_eat_add.setOnClickListener { _ ->
            startPickerActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dynamic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_send) {
            if (edit_eat_breakfast.toText().isEmpty()) {
                toast("发布内容不能为空")
                return@onOptionsItemSelected true
            }
            val dialog = getWaitDialog()
//            dialog.setUnCancel()
            ServerApi.getOCSPeriodEffectiveSignSign(2).subscribe(object : CustomNetErrorWrapper<SignInfo>() {
                override fun onNext(t: SignInfo) {
                    LogUtils.d(t)
                    uploadPicDynamics(t.data, dialog)
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    dialog.safeDismiss()
                }

            })
        }
        return super.onOptionsItemSelected(item)
    }

    fun uploadPicDynamics(t: SignInfo.Data, dialog: MaterialDialog?) {
        val uploadPics = ArrayList<DynamicSelectedPic.PicOrderInfo>()
        if (photos.size > 0) {
            photos.forEach {
                //遍历然后上传图片
                it.putPicForDynamicSelectedPic(t.sign, t.cosPath, uploadPics, photos.size, {
                    if (it.isSucceed) {
                        //图片上传完毕 开始把信息给服务端
                        commitEat(it.uploadPics!!.joinToString { it.toString() }, dialog)
                    } else {
                        //失败
                        dialog?.safeDismiss()
                    }
                })
            }
        } else {
            commitEat("", dialog)
        }

    }

    private fun commitEat(picUrls: String, dialog: MaterialDialog?) {
        ServerApi.commitEat(edit_eat_breakfast.toText(), edit_eat_lunch.toText(), edit_eat_supper.toText(), picUrls, Tools.time().millis2String(Tools.time().nowTimeMills, "yyyy-MM-dd"))
                .doOnTerminate { dialog?.safeDismiss() }.subscribe(object : CustomNetErrorWrapper<Any>() {
            override fun onNext(t: Any) {
                toast("发布成功!")
                finish()
            }
        })
    }

    @PermissionYes(200)
    fun startPickerActivity() {
        val arrayList = ArrayList<String>()
        arrayList.addAll(photos.map { it.url!!.path })
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false).
                setSelected(arrayList)
                .start(this, PhotoPicker.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                photos.clear()
                photos.addAll(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).map { DynamicSelectedPic(File(it)) })
                LogUtils.d(photos)
                val arrayList = ArrayList<String>()
                arrayList.addAll(photos.map { it.url!!.path })
                if (photos.isNotEmpty()) {
                    ll_edit_eat_pics.removeAllViews()
                    photos.forEachIndexed { index, dynamicSelectedPic ->
                        val imageView = CustomSquareProgressBar(ctx)
                        val layoutParams = LinearLayout.LayoutParams(dimen(R.dimen.edit_eat_image_width), dimen(R.dimen.edit_eat_image_width))
                        layoutParams.marginStart = dip(10)
                        imageView.layoutParams = layoutParams
                        val imageLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                        imageView.imageView.layoutParams = imageLayoutParams
                        Glide.with(ctx)
                                .load(dynamicSelectedPic.url).apply(RequestOptions.centerCropTransform())
                                .into(imageView.imageView)
                        imageView.setOnClickListener {
                            PhotoPreview.builder()
                                    .setPhotos(arrayList)
                                    .setCurrentItem(index)
                                    .setShowDeleteButton(false)
                                    .start(act)
                        }
                        dynamicSelectedPic.squareProgressBar = imageView
                        ll_edit_eat_pics.addView(imageView)
                    }

                }
            }
        }

    }

}
