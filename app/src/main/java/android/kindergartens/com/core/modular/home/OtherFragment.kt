package android.kindergartens.com.core.modular.home

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.database.TUserModel
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.auth.LoginActivity
import android.kindergartens.com.core.modular.home.data.UserProfileEntity
import android.kindergartens.com.core.modular.setting.SettingActivity
import android.kindergartens.com.core.modular.userinfo.UserInfoActivity
import android.kindergartens.com.ext.applyAndSave
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.transition.Slide
import android.support.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import kotlinx.android.synthetic.main.fragment_other.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity


class OtherFragment : BaseFragment() {

    override fun onVisible() {
        super.onVisible()
        try {
            val haveOnlineUser = UserdataHelper.haveOnlineUser()
            UserdataHelper.haveOnlineLet {

                ServerApi.getAccountProfile().subscribe(object : CustomNetErrorWrapper<UserProfileEntity>() {
                    override fun onNext(t: UserProfileEntity) {
                        //重新设置
                        setUpUserUi(UserdataHelper.selectUserByTel(it.tel!!).applyAndSave {
                            nickName = t.data.nickName
                        })
                    }

                })
                setUpUserUi(it)
            }
            TransitionManager.beginDelayedTransition(fl_login_state_parent, Slide(Gravity.TOP))
            card_noLogin.visibility = if (haveOnlineUser) View.INVISIBLE else View.VISIBLE
            card_login.visibility = if (!haveOnlineUser) View.INVISIBLE else View.VISIBLE
            acb_setting.setOnClickListener({
                startActivity<SettingActivity>()
            })
        } catch (e: NullPointerException) {
            //可能这个页面还没初始化就已经踢下线了
        }

    }

    private fun setUpUserUi(tUserModel: TUserModel) {
        tv_nickname.text = tUserModel.nickName
        Glide.with(ctx).load(tUserModel.avatar).apply(bitmapTransform(CircleCrop()))
                .into(iv_avatar)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_other, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bt_go_login.setOnClickListener {
            startActivity<LoginActivity>()
        }
        card_login.setOnClickListener {
            startActivity<UserInfoActivity>()
        }
    }

    companion object {
        fun newInstance(): OtherFragment = OtherFragment()
    }
}
