package android.kindergartens.com.core.modular.safe

import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.changepassword.ChangePasswordActivity
import android.kindergartens.com.ext.applyAndSave
import android.kindergartens.com.ext.getWaitDialog
import android.kindergartens.com.ext.safeDismiss
import android.kindergartens.com.ext.yes
import android.kindergartens.com.net.ApiException
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.mazouri.tools.Tools
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_safe.*
import org.jetbrains.anko.act
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity


class SafeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe)

        ll_change_password.setOnClickListener { startActivity<ChangePasswordActivity>() }
        setUpUi()
    }

    private fun setUpUi() {
        UserdataHelper.haveOnlineLet {
            tv_tel.text = it.tel
            tv_qq.text = if (it.qqOpenId.isNullOrEmpty()) "尚未绑定" else it.qqNickName
            tv_wechat.text = if (it.wxOpenId.isNullOrEmpty()) "尚未绑定" else it.wxNickName
            sc_bind_qq.isChecked = it.qqOpenId?.isNotEmpty() == true
            sc_bind_wechat.isChecked = it.wxOpenId?.isNotEmpty() == true
            ll_change_qq.setOnClickListener {
                if (sc_bind_qq.isChecked) {
//                    sc_bind_qq.isChecked = true
                    showHintDialog(SHARE_MEDIA.QQ.name, sc_bind_qq)
                } else {
                    UMShareAPI.get(ctx).getPlatformInfo(act, SHARE_MEDIA.QQ, authListener)
                }
            }
            ll_change_wechat.setOnClickListener {
                if (sc_bind_wechat.isChecked) {
//                    sc_bind_wechat.isChecked = true
                    showHintDialog(SHARE_MEDIA.WEIXIN.name, sc_bind_wechat)
                } else {
                    UMShareAPI.get(ctx).getPlatformInfo(act, SHARE_MEDIA.WEIXIN, authListener)
                }


            }
        }

    }

    //弹提示框
    private fun showHintDialog(platform: String, switchCompat: CompoundButton) {
        MaterialDialog.Builder(this)
                .title("提示")
                .content("解绑${platform.toUpperCase()}后你将无法使用${platform.toUpperCase()}登录小助手,你确定要解绑吗?")
                .positiveText("解绑").onPositive { _, which ->
            val dialog = getWaitDialog()
            ServerApi.unbindQQORWechat(platform).doOnTerminate { dialog.safeDismiss() }.subscribe(object : CustomNetErrorWrapper<Any>() {
                override fun onNext(t: Any) {
                    switchCompat.isChecked = false

                    UserdataHelper.getOnlineUser()?.applyAndSave {
                        if (platform == "QQ") {
                            qqOpenId = ""
                        } else if (platform == "WEIXIN") {
                            wxOpenId = ""
                        }
                        setUpUi()
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    switchCompat.isChecked = true
                }
            })
        }
                .negativeText("取消").onNegative { dialog, which ->
//            switchCompat.isChecked = !switchCompat.isChecked
        }
                .show()
    }

    var authListener: UMAuthListener = object : UMAuthListener {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        override fun onStart(platform: SHARE_MEDIA) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        override fun onComplete(platform: SHARE_MEDIA, action: Int, data: Map<String, String>) {
            sendL(platform, data)
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        override fun onError(platform: SHARE_MEDIA, action: Int, t: Throwable) {
            if (Tools.apk().isAppDebug(this@SafeActivity)) {
                sendL(platform, QQAUTODATA())
            }

            Toast.makeText(ctx, "失败：" + t.message, Toast.LENGTH_LONG).show()
        }

        fun sendL(platform: SHARE_MEDIA, data: Map<String, String>) {
            val dialog = getWaitDialog()
            ServerApi.bindQQORWechat(data["uid"]!!, data["name"]!!, data["gender"]!!, data["iconurl"]!!, platform.name)
                    .doOnTerminate { dialog.safeDismiss() }.subscribe(object : CustomNetErrorWrapper<Any>() {
                override fun onNext(it: Any) {
                    //刷新下当前界面
                    UserdataHelper.getOnlineUser()?.applyAndSave {
                        if (platform.toString() == "QQ") {
                            qqOpenId = data["uid"]
                        } else if (platform.toString() == "WEIXIN") {
                            wxOpenId = data["uid"]
                        }
                        avatar.isNullOrEmpty().yes {
                            avatar = data["iconurl"]
                        }
                        setUpUi()
                    }
                    setUpUi()
                }

                override fun onCustomError(e: ApiException) {
                    super.onCustomError(e)
                    //没授权
                    if (e.code == 1004) {

                    }
                }
            })
        }

        /**
         * @desc 授权取消的回
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        override fun onCancel(platform: SHARE_MEDIA, action: Int) {
            if (Tools.apk().isAppDebug(this@SafeActivity)) {
                sendL(platform, QQAUTODATA())
            }

            Toast.makeText(ctx, "取消了", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        fun QQAUTODATA(): HashMap<String, String> {
            val data = HashMap<String, String>()
            data["uid"] = "9353405EEC487A7A4B39148E16F13C87"
            data["name"] = "牛顿"
            data["iconurl"] = "http://q.qlogo.cn/qqapp/1105658225/9353405EEC487A7A4B39148E16F13C87/100"
            data["gender"] = "男"
            return data
        }
    }
}
