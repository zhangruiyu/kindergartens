package android.kindergartens.com.core.modular.classroom

import android.content.pm.ActivityInfo
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.classroom.data.ClassroomEntity
import android.kindergartens.com.core.tools.SystemBarHelper
import android.kindergartens.com.custom.ui.AppBarStateChangeEvent
import android.kindergartens.com.ext.width
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.ezvizuikit.open.EZUIError
import com.ezvizuikit.open.EZUIPlayer
import com.videogo.exception.BaseException
import com.videogo.openapi.EZOpenSDK
import kotlinx.android.synthetic.main.activity_class_room.*
import org.jetbrains.anko.toast
import java.util.*

class ClassroomActivity : BaseToolbarActivity(), View.OnClickListener, EZUIPlayer.EZUIPlayerCallBack {

    private val TAG = "ClassroomActivity"
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private var isResumePlay = false
    /**
     * The [ViewPager] that will host the section contents.
     */
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_room)
        toolbar_layout.layoutParams.height = (width.toDouble() * 0.562).toInt()
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        container.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(container)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
       /* ServerApi.getYSToken().flatMap {
            EZOpenSDK.getInstance().setAccessToken(it.data.accessToken)
            ServerApi.getClassrooms()
        }*/
        ServerApi.getClassrooms().subscribe(object : CustomNetErrorWrapper<ClassroomEntity>() {

            override fun onNext(classroomEntity: ClassroomEntity) {
                EZOpenSDK.getInstance().setAccessToken(classroomEntity.data.addition)
                mSectionsPagerAdapter.setNewData(classroomEntity.data.data)
//                doAsync {
                try {
                    val deviceInfo = classroomEntity.data.data[0].kgCamera
//                        val deviceInfo1 = EZOpenSDK.getInstance().getDeviceInfo(deviceInfo.deviceSerial)
//                        LogUtils.d(deviceInfo1)
                    val classroomImage = classroomEntity.data.data[0].classroomImage
                    preparePlay(deviceInfo, classroomImage)
                } catch (e: BaseException) {
                    toast(e.localizedMessage + e.errorCode)
                }
//                }

            }
        })
        appbar.addOnOffsetChangedListener(object : AppBarStateChangeEvent() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State, verticalOffset: Int) {
                if (state == State.COLLAPSED) {
                    cpv_play.stopPlay()
                }
            }

        })

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop + " + cpv_play.mStatus)
        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
        if (cpv_play.mStatus != EZUIPlayer.STATUS_STOP) {
            isResumePlay = true
        }
        //停止播放
        cpv_play.stopPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        cpv_play.releasePlayer()
    }

    /**
     * 准备播放资源参数
     */
    private fun preparePlay(deviceSerial: ClassroomEntity.WrapperData.Data.KgCamera, classroomImage: String) {
        //设置播放资源参数
        cpv_play.mEzUIPlayerCallBack = this
        cpv_play.setParameters(deviceSerial, classroomImage)
    }

    override fun onPlaySuccess() {
        Log.d(TAG, "onPlaySuccess")
        // TODO: 2017/2/7 播放成功处理
//        mBtnPlay.setText("暂停")
    }

    override fun onPlayFail(error: EZUIError) {
        Log.d(TAG, "onPlayFail")
        // TODO: 2017/2/21 播放失败处理
        if (error.errorString == EZUIError.UE_ERROR_INNER_VERIFYCODE_ERROR) {

        } else if (error.errorString.equals(EZUIError.UE_ERROR_NOT_FOUND_RECORD_FILES, ignoreCase = true)) {
            // TODO: 2017/5/12
            //未发现录像文件
            Toast.makeText(this, "未找到录像文件", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPrepared() {
        Log.d(TAG, "onPrepared")
        //播放
        cpv_play.startPlay()
    }

    override fun onPlayTime(calendar: Calendar?) {
        Log.d(TAG, "onPlayTime")
        if (calendar != null) {
            // TODO: 2017/2/16 当前播放时间
            Log.d(TAG, "onPlayTime calendar = " + calendar.time.toString())
        }
    }

    override fun onPlayFinish() {
        // TODO: 2017/2/16 播放结束
        Log.d(TAG, "onPlayFinish")
    }

    override fun onVideoSizeChange(width: Int, height: Int) {
        // TODO: 2017/2/16 播放视频分辨率回调
        Log.d(TAG, "onVideoSizeChange  width = $width   height = $height")
    }

    override fun onClick(view: View) {
        when (view.id) {
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        SystemBarHelper.immersiveStatusBar(this)
        SystemBarHelper.setHeightAndPadding(this, toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_class_room, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val devices: ArrayList<ClassroomEntity.WrapperData.Data> = arrayListOf()
        fun setNewData(data: List<ClassroomEntity.WrapperData.Data>) {
            devices.clear()
            devices.addAll(data)
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ClassroomFragment.newInstance(position + 1, devices[position])
        }

        override fun getCount(): Int = devices.size

        override fun getPageTitle(position: Int): CharSequence? = devices[position].showName
    }
}