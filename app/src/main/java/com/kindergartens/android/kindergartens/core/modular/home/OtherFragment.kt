package com.kindergartens.android.kindergartens.core.modular.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.core.modular.auth.LoginActivity
import com.kindergartens.android.kindergartens.ext.getColorSource
import com.kindergartens.android.kindergartens.ext.getDrawableSource
import com.utils.kindergartens.yes
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.UI
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.nestedScrollView

class OtherFragment : BaseFragment() {

    override fun onVisible() {
        super.onVisible()
        debug { "成功了吗" }
        val haveOnlineUser = UserdataHelper.haveOnlineUser()
        val ll_noLogin = find<LinearLayout>(ll_noLogin_id)
        val ll_loaned = find<LinearLayout>(ll_loaned_id)
        val tv_tel = find<TextView>(tv_tel_id)
        val tv_hint = find<TextView>(tv_hint_id)
        haveOnlineUser.yes {
            tv_hint.text = "123"
            tv_tel.setOnClickListener { UserdataHelper.loginOut { onVisible() } }
        }
        ll_noLogin.visibility = if (haveOnlineUser) View.INVISIBLE else View.VISIBLE
        ll_loaned.visibility = if (!haveOnlineUser) View.INVISIBLE else View.VISIBLE
        tv_tel.visibility = if (!haveOnlineUser) View.INVISIBLE else View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return UI {
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
        }.view
    }


    companion object {
        const val ll_noLogin_id = 123123
        const val ll_loaned_id = 2123123
        const val tv_tel_id = 3123123
        const val tv_hint_id = 4
        fun newInstance(): OtherFragment {
            val fragment = OtherFragment()
            return fragment
        }
    }
}
