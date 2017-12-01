package android.kindergartens.com.core.modular.auth

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.auth.data.LoginUserEntity
import android.kindergartens.com.ext.applyAndSave
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
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast

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
        et_password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.onClick {
            attemptLogin()
            /*   ServerApi.getYSToken<YSAccessToken>().subscribe(object : Observer<LzyResponse<YSAccessToken>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: LzyResponse<YSAccessToken>) {
                        debug{t}
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onComplete() {
                    }
                })*/
        }
        tv_help.onClick {
            Snackbar.make(ll_root, "121", Snackbar.LENGTH_SHORT).show()
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
        et_password.error = null

        // Store values at the time of the login attempt.
        val emailText = tel.text.toString()
        val passwordText = et_password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(passwordText) && !isPasswordValid(passwordText)) {
            et_password.error = getString(R.string.error_invalid_password)
            focusView = et_password
            cancel = true
        }

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
            ServerApi.login(tel.toText(), et_password.toText()).subscribe(object : CustomNetErrorWrapper<LoginUserEntity>() {
                override fun onNext(it: LoginUserEntity) {
                    UserdataHelper.selectUserByTel(it.data.tel).applyAndSave {
                        isOnline = true
                        tel = it.data.tel
                        id = it.data.id
                        token = it.data.token
                        gender = it.data.gender
                        address = it.data.address
                        relation = it.data.relation

                    }
                    activity?.finish()
                }
            })
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true)
//            mAuthTask = UserLoginTask(email, password)
//            mAuthTask!!.execute(null as Void?)
            toast("Hi there!")
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun isTelValid(email: String): Boolean {
        return email.length == 11
    }
}