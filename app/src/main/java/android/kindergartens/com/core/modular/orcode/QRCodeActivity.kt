package android.kindergartens.com.core.modular.orcode

import android.graphics.Bitmap
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.tools.ImgUtils
import android.kindergartens.com.ext.width
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_qrcode.*
import org.jetbrains.anko.act
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast


class QRCodeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)
        Glide.with(this).asBitmap().load(UserdataHelper.getOnlineUser()!!.avatar!!).transition(BitmapTransitionOptions()).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>) {
                val mBitmap = CodeUtils.createImage("aaa", width * 2 / 3, width * 2 / 3, resource)
                aciv_qr_code.setImageBitmap(mBitmap)
                aciv_qr_code.setOnLongClickListener {
                    MaterialDialog.Builder(ctx)
                            .items("保存到手机", "分享")
                            .itemsCallback(object : MaterialDialog.ListCallback {
                                override fun onSelection(dialog: MaterialDialog, view: View, which: Int, text: CharSequence) {
                                    when (which) {
                                        0 -> {
                                            if (ImgUtils.saveImageToGallery(ctx, mBitmap)) {
                                                toast("图片保存成功")
                                            } else {
                                                toast("图片保存失败")
                                            }
                                        }
                                        1 -> {
                                            val image = UMImage(ctx, mBitmap)
                                            ShareAction(act)
                                                    .withText("我发现了一个很好的幼儿园app").withSubject("快来下载试试吧")
                                                    .withMedia(image)
                                                    .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL)
                                                    .setCallback(object : UMShareListener {
                                                        override fun onResult(p0: SHARE_MEDIA) {
                                                            LogUtils.e("onResult")
                                                        }

                                                        override fun onCancel(p0: SHARE_MEDIA?) {
                                                            LogUtils.e("onCancel")
                                                        }

                                                        override fun onError(p0: SHARE_MEDIA?, p1: Throwable) {
                                                            LogUtils.e(p1.message + "onError")
                                                        }

                                                        override fun onStart(p0: SHARE_MEDIA?) {

                                                        }

                                                    })
                                                    .open();
                                        }
                                    }
                                }
                            })
                            .show()
                    return@setOnLongClickListener true
                }
            }

        })

    }

}
