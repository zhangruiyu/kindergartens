package com.kindergartens.android.kindergartens.core.modular.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.database.TUserModel
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.core.modular.auth.LoginActivity
import com.kindergartens.android.kindergartens.core.modular.home.data.UserProfileEntity
import com.kindergartens.android.kindergartens.ext.applyAndSave
import com.kindergartens.android.kindergartens.net.CustomNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_other.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity

class OtherFragment : BaseFragment() {

    override fun onVisible() {
        super.onVisible()
        val haveOnlineUser = UserdataHelper.haveOnlineUser()
        UserdataHelper.haveOnlineLet {

            //            tv_tel.setOnClickListener { UserdataHelper.loginOut { onVisible() } }
            ServerApi.getAccountProfile().subscribe(object : CustomNetErrorWrapper<UserProfileEntity>() {
                override fun onNext(t: UserProfileEntity) {
                    //重新设置
                    setUpUserUi(UserdataHelper.selectUserByTel(it.tel!!).applyAndSave {
                        avatar = t.data.avatar
                        nickName = t.data.nickName
                    })
                }

            })
            setUpUserUi(it)
        }
        TransitionManager.beginDelayedTransition(fl_login_state_parent, Slide(Gravity.RIGHT))
        card_noLogin.visibility = if (haveOnlineUser) View.INVISIBLE else View.VISIBLE
        card_login.visibility = if (!haveOnlineUser) View.INVISIBLE else View.VISIBLE
    }

    private fun setUpUserUi(tUserModel: TUserModel) {
        tv_nickname.text = tUserModel.nickName
        Glide.with(ctx).load(tUserModel.avatar).bitmapTransform(CropCircleTransformation(ctx))
                .into(iv_avatar)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_other, null)
        /* return UI {
             nestedScrollView {
                 verticalLayout {
                     frameLayout {
                         verticalLayout {
                             id = ll_noLogin_id
                             textView("登陆小助手,体验更多功能") {
                                 textColor = getColorSource(R.color.blue)
                             }
                             textView {
                                 text = "注册/登陆"
                                 onClick {
                                     startActivity<LoginActivity>()
                                 }
                             }
                         }.lparams(gravity = Gravity.CENTER) {
                             backgroundResource = R.color.red_dark
                         }
                         verticalLayout {
                             id = ll_loaned_id
                             textView("退出登录") {
                                 id = tv_tel_id
                                 textColor = getColorSource(R.color.blue)
                             }
                             textView("退出登录") {
                                 id = tv_hint_id
                                 textColor = getColorSource(R.color.blue)
                             }
                         }.lparams(gravity = Gravity.CENTER) {
                             backgroundResource = R.color.red_dark
                         }

                     }.lparams(matchParent, dip(90)) {
                         for (i in 0..13) {
                             textView("我的等级") {
                                 textColor = getColorSource(R.color.red)
                                 val drawable = getDrawableSource(R.drawable.ic_home_normal)
                                 drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                                 setCompoundDrawables(drawable, null, null, null)
                                 leftPadding = dip(20)
                                 gravity = Gravity.CENTER_VERTICAL
                                 onClick {
 //                                    startActivity<SettingsActivity>()
                                 }
                             }.lparams(matchParent, dip(40)) {
                                 backgroundResource = R.color.white
                             }
                         }
                     }
                 }.lparams(matchParent, matchParent) {

                 }
             }
         }.view*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bt_go_login.setOnClickListener {
            startActivity<LoginActivity>()
        }
    }

    companion object {
        fun newInstance(): OtherFragment {
            val fragment = OtherFragment()
            return fragment
        }
    }
}
