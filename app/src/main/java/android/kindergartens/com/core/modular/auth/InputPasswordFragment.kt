package android.kindergartens.com.core.modular.auth

import android.kindergartens.com.Constants
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.auth.data.LoginUserEntity
import android.kindergartens.com.ext.toText
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_inputpassword.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by zhangruiyu on 2017/6/27.
 */
class InputPasswordFragment : BaseFragment() {
    var tel: String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inputpassword, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //获取本地的预输入手机号
        et_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        submitbutton.onClick {
            attemptLogin()
        }
        tv_help.onClick {
            Snackbar.make(ll_root, "请联系幼儿园", Snackbar.LENGTH_SHORT).show()
        }
    }


    private fun attemptLogin() {

        // Reset errors.
        et_password.error = null

        // Store values at the time of the login attempt.
        val passwordText = et_password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(passwordText) && !isPasswordValid(passwordText)) {
            et_password.error = getString(R.string.error_invalid_password)
            focusView = et_password
            cancel = true
        }


        if (cancel) {
            focusView!!.requestFocus()
        } else {
            ServerApi.login(tel, et_password.toText(), Constants.PushToken).compose(this.bindUntilEvent(com.trello.rxlifecycle2.android.FragmentEvent.DESTROY)).subscribe(object : CustomNetErrorWrapper<LoginUserEntity>() {
                override fun onNext(it: LoginUserEntity) {
                    UserdataHelper.saveLoginUser(it)
                    submitbutton.doResult(true)
                    activity?.finish()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    submitbutton.doResult(false)
                }
            })
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

}