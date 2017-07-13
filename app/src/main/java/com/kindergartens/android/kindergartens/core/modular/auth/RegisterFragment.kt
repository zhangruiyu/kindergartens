package com.kindergartens.android.kindergartens.core.modular.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.ext.toText
import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by zhangruiyu on 2017/6/27.
 */
class RegisterFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        et_auth_code.toText()
        bt_send_auth_code.onClick {
           /* http<LzyResponse<String>> {
                url = "/auth/register1"
                params = mapOf("tel" to tel.toText())
                onSuccess {
                    debug { it }
                }
                onFail {
                    debug { it }
                }
            }*/
        }
        bt_register.setOnClickListener {
          /*  http<LzyResponse<Any>> {
                url = "/auth/register2"
                params = mapOf("tel" to tel.toText(), "password" to et_password.toText(), "authCode" to et_auth_code.toText())
                onSuccess {
                    debug { it }
                }
                onFail {
                    debug { it }
                }
            }*/
        }
    }
    /*http<LzyResponse<YSAccessToken>> {
        url = "https://open.ys7.com/api/lapp/token/get"
        params = mapOf("appKey" to "b109fdee59b14b19b48927f627814c58", "appSecret" to "fa7d8a8c75176be997d80f13590dfaa6")
        onSuccess {
            debug { it }
        }
        onFail {
            debug { it }
        }
    }*/
}