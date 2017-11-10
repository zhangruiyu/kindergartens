package com.kindergartens.android.kindergartens.core.modular.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.core.modular.safe.SafeActivity
import kotlinx.android.synthetic.main.fragment_setting.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * A placeholder fragment containing a simple view.
 */
class SettingActivityFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val haveOnlineUser = UserdataHelper.haveOnlineUser()
        bt_login_out.setOnClickListener {
            UserdataHelper.loginOut()
            activity.finish()
        }
        bt_login_out.visibility = if (!haveOnlineUser) View.INVISIBLE else View.VISIBLE
        bt_account_safe.setOnClickListener({
            UserdataHelper.haveNoOnlineLet { startActivity<SafeActivity>() }

        })
    }
}
