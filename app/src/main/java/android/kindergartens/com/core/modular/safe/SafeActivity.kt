package android.kindergartens.com.core.modular.safe

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.changepassword.ChangePasswordActivity
import android.os.Bundle
import android.widget.CompoundButton
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_safe.*
import org.jetbrains.anko.startActivity


class SafeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe)
        UserdataHelper.haveOnlineLet {
            tv_tel.text = it.tel
            tv_qq.text = if (it.qqOpenId.isNullOrEmpty()) "尚未绑定" else it.qqNickName
            tv_wechat.text = if (it.wxOpenId.isNullOrEmpty()) "尚未绑定" else it.wxNickName
            sc_bind_qq.isChecked = it.qqOpenId?.isNotEmpty() == true
            sc_bind_wechat.isChecked = it.wxOpenId?.isNotEmpty() == true
            sc_bind_qq.setOnClickListener {
                showHintDialog("qq", it as CompoundButton)

            }
            sc_bind_wechat.setOnClickListener {
                if (sc_bind_wechat.isChecked) {
                    showHintDialog("wechat", it as CompoundButton)
                } else {

                }


            }
        }
        ll_change_password.setOnClickListener { startActivity<ChangePasswordActivity>() }

    }

    private fun showHintDialog(platform: String, switchCompat: CompoundButton) {
        MaterialDialog.Builder(this)
                .title("提示")
                .content("解绑${platform.toUpperCase()}后你将无法使用${platform.toUpperCase()}登录小助手,你确定要解绑吗?")
                .positiveText("解绑").onPositive { dialog, which ->

        }
                .negativeText("取消").onNegative { dialog, which ->
            switchCompat.isChecked = !switchCompat.isChecked
        }
                .show()
    }

}
