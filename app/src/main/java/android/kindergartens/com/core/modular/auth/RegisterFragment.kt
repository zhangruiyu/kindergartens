package android.kindergartens.com.core.modular.auth

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.ext.toText
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * Created by zhangruiyu on 2017/6/27.
 */
class RegisterFragment : BaseFragment() {
    var tel: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        et_auth_code.toText()
        bt_send_auth_code.onClick {
            ServerApi.sendRegisterCode(tel).subscribe(object : CustomNetErrorWrapper<Any>() {
                override fun onNext(t: Any) {
                    toast("验证码发送成功")
                }

            })
        }
        submitbutton.setOnClickListener {
            ServerApi.registerUser(tel, acact_password.toText(), et_auth_code.toText()).subscribe(object : CustomNetErrorWrapper<Any>() {
                override fun onNext(t: Any) {
                    toast("注册成功,请登录")
                    activity?.finish()
                }

            })
        }
    }
}