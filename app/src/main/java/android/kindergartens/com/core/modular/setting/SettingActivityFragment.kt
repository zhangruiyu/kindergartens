package android.kindergartens.com.core.modular.setting

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.safe.SafeActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
