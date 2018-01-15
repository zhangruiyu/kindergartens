package android.kindergartens.com.core.modular.auth

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.modular.auth.data.CodeWrapperEntity
import android.kindergartens.com.ext.toText
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.ctx

/**
 * Created by zhangruiyu on 2017/6/27.
 */
class LoginFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //获取本地的预输入手机号
        onLoadFinished()
        submitbutton.onClick {
            attemptLogin()
        }
        tv_help.onClick {
            Snackbar.make(ll_root, "121", Snackbar.LENGTH_SHORT).show()
            submitbutton.reset()
        }
    }


    fun onLoadFinished() {
        val tels = ArrayList<String>()
        tels.add("15201231801")
        addTelsToAutoComplete(tels)
    }

    private fun addTelsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(ctx,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        tel.setAdapter(adapter)
    }

    private fun attemptLogin() {

        // Reset errors.
        tel.error = null

        // Store values at the time of the login attempt.
        val emailText = tel.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(emailText)) {
            tel.error = getString(R.string.error_field_required)
            focusView = tel
            cancel = true
        } else if (!isTelValid(emailText)) {
            tel.error = getString(R.string.error_invalid_email)
            focusView = tel
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            ServerApi.verifyIsRegister(tel.toText()).doOnTerminate {
                submitbutton.reset()
            }.subscribe(object : CustomNetErrorWrapper<CodeWrapperEntity>() {
                override fun onNext(it: CodeWrapperEntity) {
                    if (it.data.data == "0") {
                        (act as LoginActivity).switchRegisterFragment(tel.toText())
                    } else {
                        (act as LoginActivity).switchInputPasswordFragment(tel.toText())
                    }
                    submitbutton.doResult(true)
//                    activity?.finish()
                }

            })
        }
    }


    private fun isTelValid(email: String): Boolean {
        return email.length == 11
    }
}