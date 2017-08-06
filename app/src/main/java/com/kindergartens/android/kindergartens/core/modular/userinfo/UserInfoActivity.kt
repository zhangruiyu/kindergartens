package com.kindergartens.android.kindergartens.core.modular.userinfo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.ext.*
import com.kindergartens.android.kindergartens.net.CustomNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionYes
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_userinfo.*
import me.iwf.photopicker.PhotoPicker
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.io.File

class UserInfoActivity : BaseToolbarActivity() {
    val relationIds = arrayOf(R.id.acrb_one, R.id.acrb_two, R.id.acrb_three, R.id.acrb_four, R.id.acrb_five, R.id.acrb_six, R.id.acrb_seven)
    lateinit var avatarUrl: String
    //选择的图片的本地路径
    var localFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndPermission.with(ctx).requestCode(200).permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA).callback(this).start()
        setContentView(R.layout.activity_userinfo)
        UserdataHelper.haveOnlineLet {
            Glide.with(ctx).load(it.avatar).bitmapTransform(CropCircleTransformation(ctx))
                    .into(iv_avatar)
            avatarUrl = it.avatar!!
            edt_nick_name.hint = it.nickName
            edt_nick_name.canNotEdit()
            met_address.setText(it.address)
//            find<AppCompatRadioButton>(relationIds[it.relation!!]).isChecked = true
            krgv_check_group.check(relationIds[it.relation!!])
            met_address.setText(it.address)
            if (it.gender == 1) {
                accb_man.isChecked = true
            } else {
                accb_men.isChecked = true
            }
            accb_man.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    accb_men.isChecked = false
                }
            }
            accb_men.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    accb_man.isChecked = false
                }
            }
        }

        iv_avatar.setOnClickListener {
            startPickerActivity()
        }
    }

    @PermissionYes(200)
    fun startPickerActivity() {
        PhotoPicker.builder()
                .setPhotoCount(1)
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
                if (photos.size > 0) {
                    localFile = photos[0]
                    Glide.with(ctx).load(File(localFile)).bitmapTransform(CropCircleTransformation(ctx))
                            .into(iv_avatar)
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_send) {
            UserdataHelper.haveOnlineLet {
                onlineUser ->
                val checkGender = if (accb_man.isChecked) 1 else 0
                val relationCheck = find<View>(krgv_check_group.checkedRadioButtonId).tag.toString().toInt()
                if (checkGender == onlineUser.gender && relationCheck == onlineUser.relation && met_address.toText() == onlineUser.address && avatarUrl == onlineUser.avatar) {
                    toast("请修改后在点击提交按钮")
                    return@haveOnlineLet
                } else {
                    val dialog = getWaitDialog("正在提交修改的信息,请稍等!")
                    if (localFile != null) {

                    }
                    ServerApi.reviseProfile(checkGender, relationCheck, met_address.toText(), avatarUrl).compose(bindToLifecycle())
                            .doOnTerminate { dialog.safeDismiss() }.subscribe(object : CustomNetErrorWrapper<Any>() {
                        override fun onNext(t: Any) {
                            UserdataHelper.getOnlineUser()?.applyAndSave {
                                avatar = onlineUser.avatar
                                address = met_address.toText()
                                gender = checkGender
                                relation = relationCheck
                            }
                            finish()
                        }

                    })
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dynamic, menu)
        return true
    }
}
