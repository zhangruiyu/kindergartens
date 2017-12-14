package android.kindergartens.com.core.modular.safe

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.tools.CustomNextInputs
import android.kindergartens.com.ext.TSnackbarUtils
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import com.github.yoojia.inputs.ValueScheme
import kotlinx.android.synthetic.main.activity_safe.*

class SafeActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe)
        val passwordNextInputs = CustomNextInputs()
        passwordNextInputs.add(acat_old_password, ValueScheme.RangeLength(6, 15).msg("原密码输入格式有误"))
                .add(acat_new_password, ValueScheme.RangeLength(6, 15).msg("新密码输入格式有误"))
                .add(acat_again_password, ValueScheme.RangeLength(6, 15).msg("再次输入的密码格式有误"))
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
