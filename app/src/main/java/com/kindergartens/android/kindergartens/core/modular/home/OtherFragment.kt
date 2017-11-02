package com.kindergartens.android.kindergartens.core.modular.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.database.TUserModel
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.core.modular.auth.LoginActivity
import com.kindergartens.android.kindergartens.core.modular.home.data.UserProfileEntity
import com.kindergartens.android.kindergartens.core.modular.setting.SettingActivity
import com.kindergartens.android.kindergartens.core.modular.userinfo.UserInfoActivity
import com.kindergartens.android.kindergartens.ext.applyAndSave
import com.kindergartens.android.kindergartens.net.CustomNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.fragment_other.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity


class OtherFragment : BaseFragment() {

    override fun onVisible() {
        super.onVisible()
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
