package android.kindergartens.com.core.modular.home

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.database.TUserModel
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.auth.LoginActivity
import android.kindergartens.com.core.modular.home.data.UserProfileEntity
import android.kindergartens.com.core.modular.orcode.QRCodeActivity
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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.fragment_other.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivity


class OtherFragment : BaseFragment() {

    override fun onVisible() {
        super.onVisible()
        try {
            aciv_qr_code.setOnClickListener({
                startActivity<QRCodeActivity>()
            })
            val haveOnlineUser = UserdataHelper.haveOnlineUser()
            UserdataHelper.haveOnlineLet {

                ServerApi.getAccountProfile().subscribe(object : CustomNetErrorWrapper<UserProfileEntity>() {
                    override fun onNext(t: UserProfileEntity) {
                        //重新设置
                        setUpUserUi(UserdataHelper.selectUserByTel(it.tel!!).applyAndSave {
                            nickName = t.data.nickName
                            roleCode = t.data.roleCode
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
        aciv_phone_login.setOnClickListener {
            startActivity<LoginActivity>()
        }
        card_login.setOnClickListener {
            startActivity<UserInfoActivity>()
        }
        acb_store.setOnClickListener {
            startActivity<UserInfoActivity>()
        }
        aciv_qq_login.setOnClickListener {
            UMShareAPI.get(ctx).getPlatformInfo(act, SHARE_MEDIA.QQ, authListener)
        }
    }

    var authListener: UMAuthListener = object : UMAuthListener {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        override fun onStart(platform: SHARE_MEDIA) {

        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        override fun onComplete(platform: SHARE_MEDIA, action: Int, data: Map<String, String>) {

            Toast.makeText(ctx, "成功了${data["name"]}", Toast.LENGTH_LONG).show()
            ServerApi.commitQQWeixinLogin(data["uid"]!!, data["name"]!!, data["gender"]!!, data["iconurl"]!!)
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        override fun onError(platform: SHARE_MEDIA, action: Int, t: Throwable) {

            Toast.makeText(ctx, "失败：" + t.message, Toast.LENGTH_LONG).show()
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        override fun onCancel(platform: SHARE_MEDIA, action: Int) {
            Toast.makeText(ctx, "取消了", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance(): OtherFragment = OtherFragment()
    }
}
