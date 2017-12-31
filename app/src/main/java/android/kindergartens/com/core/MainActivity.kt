package android.kindergartens.com.core

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragmentActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.auth.LoginActivity
import android.kindergartens.com.core.modular.checkupdate.CustomVersionDialogActivity
import android.kindergartens.com.core.modular.dynamic.EditDynamicActivity
import android.kindergartens.com.core.modular.home.HomepageFragment
import android.kindergartens.com.core.modular.home.OtherFragment
import android.kindergartens.com.core.modular.home.data.SchoolEntity
import android.kindergartens.com.core.modular.home.dummy.DynamicFragment
import android.kindergartens.com.core.modular.scancode.CaptureActivity
import android.kindergartens.com.core.modular.scancode.CaptureActivity.Companion.CAPTURE_REQUEST_CODE
import android.kindergartens.com.core.modular.video.MediaRecorderActivity
import android.kindergartens.com.core.modular.video.TCVideoSettingActivity
import android.kindergartens.com.core.tools.DialogManager
import android.kindergartens.com.core.tools.UrlUtils
import android.kindergartens.com.ext.hideButton
import android.kindergartens.com.ext.showButton
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.kindergartens.com.service.CheckUpdateService
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import android.widget.Toast
import com.allenliu.versionchecklib.core.AllenChecker
import com.allenliu.versionchecklib.core.VersionParams
import com.allenliu.versionchecklib.core.http.HttpParams
import com.allenliu.versionchecklib.core.http.HttpRequestMethod
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.mazouri.tools.Tools
import com.tencent.ugc.TXRecordCommon
import com.uuzuche.lib_zxing.activity.CodeUtils
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionYes
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class MainActivity : BaseFragmentActivity() {
    private val ids = intArrayOf(R.color.accent, R.color.blue, R.color.grey, R.color.orange)
    private var currentTabIndex: Int = 0
    private var index: Int = 0
    private var mTextMessage: TextView? = null
    var homepageFragment = HomepageFragment()
    val dynamicFragment = DynamicFragment()
    val homepageFragment3 = OtherFragment.newInstance()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
        /* R.id.navigation_home -> {
             mTextMessage!!.setText(R.string.title_home)
             return@OnNavigationItemSelectedListener true
         }
         R.id.navigation_dashboard -> {
             mTextMessage!!.setText(R.string.title_dashboard)
             return@OnNavigationItemSelectedListener true
         }
         R.id.navigation_notifications -> {
             mTextMessage!!.setText(R.string.title_notifications)
             return@OnNavigationItemSelectedListener true
         }*/
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        AlarmUtils.setAlarm(this)

        instance = this
        initBottomNavigationBar()
        initFragments()
        val httpParams = HttpParams()
        httpParams.put("version", Tools.appTool().getAppVersionCode(ctx))
        httpParams.put("os", "Android")
        //服务不需要关闭,检查更新完后自动会关闭
        val builder = VersionParams.Builder().setRequestMethod(HttpRequestMethod.POST).setRequestUrl(ServerApi.baseUrl + "/checkUpdate").setRequestParams(httpParams)
                .setService(CheckUpdateService::class.java).setCustomDownloadActivityClass(
                CustomVersionDialogActivity::class.java
        )
        AllenChecker.startVersionCheck(this, builder.build())


//        val queryList = SQLite.select().from(TUserWrapper::class.java).where(TUserWrapper_Table.token.eq("kgg")).queryList()
//        val navigation = findViewById(R.id.navigation) as BottomNavigationView
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//        print(queryList)
        aciv_scan_code.setOnClickListener {
            toast("扫码付学费开发ing")
            val intent = Intent(this@MainActivity, CaptureActivity::class.java)
            startActivityForResult(intent, CAPTURE_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data.extras ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    val urlRequest = UrlUtils.URLRequest(result)
                    if (urlRequest.containsKey("action")) {
                        val action = urlRequest["action"]
                        when (action) {
                            "join" -> {
                                urlRequest["school_id"]?.let { showJoinSchoolDialog(it) }
                            }
                            "pay" -> toast("开发中")
                            else -> toast("解析二维码参数出现问题")
                        }
                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show()
                    } else {
                        toast("无效二维码")
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showJoinSchoolDialog(schoolId: String) {
        val onlineUser = UserdataHelper.getOnlineUser()
        if (onlineUser != null) {
            ServerApi.getSchoolInfo(schoolId).subscribe(object : CustomNetErrorWrapper<SchoolEntity>() {
                override fun onNext(t: SchoolEntity) {
                    val joinSchoolDialog = DialogManager.getJoinSchoolDialog(ctx, t.data.schoolName, t.data.address, t.data.tel, t.data.shcoolPicture)
                    joinSchoolDialog.show()
                }

            })
        } else {

        }

    }

    override fun onResume() {
        super.onResume()
        tv_school_name.setOnClickListener {
            startActivity<LoginActivity>()
        }
        if (UserdataHelper.haveOnlineUser()) {
            if (UserdataHelper.getOnlineUser()?.schoolName?.isNotEmpty() == true) {
                tv_school_name.text = UserdataHelper.getOnlineUser()?.schoolName
                tv_school_name.setOnClickListener {}
            } else {
                tv_school_name.text = "扫码加入校园"
            }

        } else {
            tv_school_name.text = "点击登录"
        }
    }

    private fun initFragments() {
        add(fragment = homepageFragment)
        add(fragment = dynamicFragment)
        add(fragment = homepageFragment3)


        supportFragmentManager.beginTransaction().add(R.id.homepage_container, homepageFragment)
                .show(homepageFragment).commit()
    }

    private fun initBottomNavigationBar() {
        bottom_navigation_bar.setMode(BottomNavigationBar.MODE_SHIFTING)
        bottom_navigation_bar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE
                )
//        bottom_navigation_bar.addItem(BottomNavigationItem(R.drawable.ic_home_normal).setActiveColorResource(R.color.accent))
//                .addItem(BottomNavigationItem(R.drawable.ic_notifications_normal).setActiveColorResource(R.color.blue))
//                .addItem(BottomNavigationItem(R.drawable.ic_menu_normal).setActiveColorResource(R.color.grey))
//                //                .setFirstSelectedPosition(1)
//                .initialise()
        bottom_navigation_bar.addItem(BottomNavigationItem(R.drawable.ic_home_normal).setActiveColorResource(R.color.accent))
                .addItem(BottomNavigationItem(R.drawable.ic_notifications_normal).setActiveColorResource(R.color.accent))
                .addItem(BottomNavigationItem(R.drawable.ic_menu_normal).setActiveColorResource(R.color.accent))
                //                .setFirstSelectedPosition(1)
                .initialise()
        bottom_navigation_bar.setFab(fab_home)
        fab_home.setOnClickListener {
            startActivity<EditDynamicActivity>()
        }
        AndPermission.with(ctx).requestCode(201).permission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).callback(this).start()
        fab_home.setOnLongClickListener {
            /*  // 录制
              val config = MediaRecorderConfig.Buidler()

                      .fullScreen(false)
                      .smallVideoWidth(360)
                      .smallVideoHeight(480)
                      .recordTimeMax(6000)
                      .recordTimeMin(1500)
                      .maxFrameRate(20)
                      .videoBitrate(600000)
                      .captureThumbnailsTime(1)
                      .build()*/

            startMediaRecorderActivity()
            return@setOnLongClickListener true
        }
        bottom_navigation_bar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {
            }

            override fun onTabUnselected(position: Int) = Unit

            override fun onTabSelected(position: Int) {
                changeFragmentByIndex(position)
//                toolbar!!.setBackgroundResource(ids[position])
                fab_home?.apply {
                    if (position == 2) {
                        hideButton()
                    } else {
                        showButton()
                    }
                }

            }

        })
//        toolbar!!.backgroundResource = ids[bottom_navigation_bar.currentSelectedPosition]
    }

    fun goSmallVideoRecorder(context: Activity, overGOActivityName: String) {
        //            //视频比例
        val mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16
        //画质
        val mRecommendQuality = TXRecordCommon.VIDEO_QUALITY_MEDIUM
//            val mRecommendQuality = TXRecordCommon.VIDEO_QUALITY_HIGH
        context.startActivity<MediaRecorderActivity>(TCVideoSettingActivity.RECORD_CONFIG_MIN_DURATION to 5 * 1000,
                TCVideoSettingActivity.RECORD_CONFIG_MAX_DURATION to 60 * 1000,
                TCVideoSettingActivity.RECORD_CONFIG_ASPECT_RATIO to mAspectRatio,
                TCVideoSettingActivity.RECORD_CONFIG_RECOMMEND_QUALITY to mRecommendQuality)
    }

    @PermissionYes(201)
    private fun startMediaRecorderActivity() {
        this.goSmallVideoRecorder(this@MainActivity, EditDynamicActivity::class.java.name)
    }

    private fun changeFragmentByIndex(currentIndex: Int) {
        index = currentIndex
        switchFragment()
    }

    private fun switchFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(mFragments!![currentTabIndex])
        if (!mFragments!![index].isAdded) {
            fragmentTransaction.add(R.id.homepage_container, mFragments!![index])
        }
        fragmentTransaction.show(mFragments!![index]).commit()
        currentTabIndex = index
        if (currentTabIndex == 1) {
            (mFragments!![1] as DynamicFragment).initData()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: MainActivity? = null
    }

}

