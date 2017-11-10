package com.kindergartens.android.kindergartens.core.modular.safe

import android.os.Bundle
import com.github.yoojia.inputs.AndroidMessageDisplay
import com.github.yoojia.inputs.AndroidNextInputs
import com.github.yoojia.inputs.ValueScheme
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import com.kindergartens.android.kindergartens.ext.TSnackbarUtils
import com.kindergartens.android.kindergartens.net.CustomNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import kotlinx.android.synthetic.main.activity_safe.*

class SafeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe)
        val passwordNextInputs = AndroidNextInputs()
        passwordNextInputs.add(acat_old_password, ValueScheme.RangeLength(6, 15).msg("原密码输入格式有误"))
                .add(acat_new_password, ValueScheme.RangeLength(6, 15).msg("新密码输入格式有误"))
                .add(acat_again_password, ValueScheme.RangeLength(6, 15).msg("再次输入的密码格式有误"))
        passwordNextInputs.setMessageDisplay(AndroidMessageDisplay())
        acb_affirm.setOnClickListener({
            if (passwordNextInputs.test()) {
                if (acat_again_password.text === acat_again_password.text) {
                    ServerApi.changePassword(acat_old_password.text.toString(), acat_new_password.text.toString())
                            .subscribe(object : CustomNetErrorWrapper<Any>() {
                                override fun onNext(t: Any) {
                                    TSnackbarUtils.toSuccess(this@SafeActivity, "密码修改成功", { finish() }).show()
                                }

                            })
                } else {
                    acat_again_password.error = "2次密码密码输入不一致"
                }
            }
        })
    }
}
