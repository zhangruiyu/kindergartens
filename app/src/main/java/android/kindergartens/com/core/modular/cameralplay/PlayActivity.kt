package android.kindergartens.com.core.modular.cameralplay

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Point
import android.kindergartens.com.R
import android.kindergartens.com.core.modular.classroommessage.WindowSizeChangeNotifier
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.ezvizuikit.open.EZUIError
import com.ezvizuikit.open.EZUIKit
import com.ezvizuikit.open.EZUIPlayer
import com.videogo.util.LogUtil
import kotlinx.android.synthetic.main.activity_play.*
import java.util.*

/**
 * 预览界面
 */
class PlayActivity : Activity(), View.OnClickListener, WindowSizeChangeNotifier.OnWindowSizeChangedListener, EZUIPlayer.EZUIPlayerCallBack {
    private var mEZUIPlayer: CustomEZUIPlayer? = null

    private var mBtnPlay: Button? = null
    /**
     * onresume时是否恢复播放
     */
    private var isResumePlay = false

    private var mOrientationDetector: MyOrientationDetector? = null

    /**
     * 开发者申请的Appkey
     */
    private var deviceSerial: String? = null
    /**
     * 授权accesstoken
     */
    private var verifyCode: String? = null
    /**
     * 播放url：ezopen协议
     */
    private var cameraNo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)
        LogUtil.d(TAG, "onCreate")
        setContentView(R.layout.activity_play)
        val intent = intent
        deviceSerial = intent.getStringExtra(DEVICESERIAL)
        verifyCode = intent.getStringExtra(VERIFYCODE)
        cameraNo = intent.getIntExtra(CAMERANO, 1)
        if (TextUtils.isEmpty(deviceSerial)) {
            Toast.makeText(this, "deviceSerial is null", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        mOrientationDetector = MyOrientationDetector(this)
        WindowSizeChangeNotifier(this, this)
        mBtnPlay = findViewById<View>(R.id.btn_play) as Button

        //获取EZUIPlayer实例
        mEZUIPlayer = findViewById<View>(R.id.player_ui) as CustomEZUIPlayer
        //设置加载需要显示的view
        mEZUIPlayer!!.setLoadingView(initProgressBar())

        mBtnPlay!!.setOnClickListener(this)
        mBtnPlay!!.setText(R.string.string_stop_play)
        preparePlay()
        setSurfaceSize()
        val mHideFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        } else {
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
        activity_play.systemUiVisibility = mHideFlags
    }

    /**
     * 创建加载view
     *
     * @return
     */
    private fun initProgressBar(): ProgressBar {
        val mProgressBar = ProgressBar(this)
        val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        lp.addRule(RelativeLayout.CENTER_IN_PARENT)
        mProgressBar.indeterminateDrawable = resources.getDrawable(R.drawable.progress)
        mProgressBar.layoutParams = lp
        return mProgressBar
    }

    /**
     * 准备播放资源参数
     */
    private fun preparePlay() {
        //设置debug模式，输出log信息
        EZUIKit.setDebug(true)
        //设置播放资源参数
        mEZUIPlayer!!.setCallBack(this)
        mEZUIPlayer!!.setUrl(deviceSerial, verifyCode, cameraNo)
    }

    override fun onResume() {
        super.onResume()
        mOrientationDetector!!.enable()
        Log.d(TAG, "onResume")
        //界面stop时，如果在播放，那isResumePlay标志位置为true，resume时恢复播放
        if (isResumePlay) {
            isResumePlay = false
            mBtnPlay!!.setText(R.string.string_stop_play)
            mEZUIPlayer!!.startPlay()
        }
    }

    override fun onPause() {
        super.onPause()
        mOrientationDetector!!.disable()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop + " + mEZUIPlayer!!.status)
        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
        if (mEZUIPlayer!!.status != EZUIPlayer.STATUS_STOP) {
            isResumePlay = true
        }
        //停止播放
        mEZUIPlayer!!.stopPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        //释放资源
        mEZUIPlayer!!.releasePlayer()
    }

    override fun onPlaySuccess() {
        Log.d(TAG, "onPlaySuccess")
        // TODO: 2017/2/7 播放成功处理
        mBtnPlay!!.setText(R.string.string_pause_play)
    }

    override fun onPlayFail(error: EZUIError) {
        Log.d(TAG, "onPlayFail")
        // TODO: 2017/2/21 播放失败处理
        if (error.errorString == EZUIError.UE_ERROR_INNER_VERIFYCODE_ERROR) {

        } else if (error.errorString.equals(EZUIError.UE_ERROR_NOT_FOUND_RECORD_FILES, ignoreCase = true)) {
            // TODO: 2017/5/12
            //未发现录像文件
            Toast.makeText(this, getString(R.string.string_not_found_recordfile), Toast.LENGTH_LONG).show()
        }
    }

    override fun onVideoSizeChange(width: Int, height: Int) {
        // TODO: 2017/2/16 播放视频分辨率回调
        Log.d(TAG, "onVideoSizeChange  width = $width   height = $height")
    }

    override fun onPrepared() {
        Log.d(TAG, "onPrepared")
        //播放
        mEZUIPlayer!!.startPlay()
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


    override fun onClick(view: View) {
        if (view === mBtnPlay) {
            // TODO: 2017/2/14
            if (mEZUIPlayer!!.status == EZUIPlayer.STATUS_PLAY) {
                //播放状态，点击停止播放
                mBtnPlay!!.setText(R.string.string_start_play)
                mEZUIPlayer!!.stopPlay()
            } else if (mEZUIPlayer!!.status == EZUIPlayer.STATUS_STOP) {
                //停止状态，点击播放
                mBtnPlay!!.setText(R.string.string_stop_play)
                mEZUIPlayer!!.startPlay()
            }
        }
    }


    /**
     * 屏幕旋转时调用此方法
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
        setSurfaceSize()
    }

    private fun setSurfaceSize() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val isWideScrren = mOrientationDetector!!.isWideScrren
        //竖屏
        if (!isWideScrren) {
            //竖屏调整播放区域大小，宽全屏，高根据视频分辨率自适应
            mEZUIPlayer!!.setSurfaceSize(dm.widthPixels, 0)
        } else {
            //横屏屏调整播放区域大小，宽、高均全屏，播放区域根据视频分辨率自适应
            mEZUIPlayer!!.setSurfaceSize(dm.widthPixels, dm.heightPixels)
        }
    }

    override fun onWindowSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        if (mEZUIPlayer != null) {
            setSurfaceSize()
        }
    }

    inner class MyOrientationDetector(context: Context) : OrientationEventListener(context) {

        private val mWindowManager: WindowManager
        private var mLastOrientation = 0

        val isWideScrren: Boolean
            get() {
                val display = mWindowManager.defaultDisplay
                val pt = Point()
                display.getSize(pt)
                return pt.x > pt.y
            }

        init {
            mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }

        override fun onOrientationChanged(orientation: Int) {
            val value = getCurentOrientationEx(orientation)
            if (value != mLastOrientation) {
                mLastOrientation = value
                val current = requestedOrientation
                if (current == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || current == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                }
            }
        }

        private fun getCurentOrientationEx(orientation: Int): Int {
            var value = 0
            if (orientation >= 315 || orientation < 45) {
                // 0度
                value = 0
                return value
            }
            if (orientation >= 45 && orientation < 135) {
                // 90度
                value = 90
                return value
            }
            if (orientation >= 135 && orientation < 225) {
                // 180度
                value = 180
                return value
            }
            if (orientation >= 225 && orientation < 315) {
                // 270度
                value = 270
                return value
            }
            return value
        }
    }

    companion object {
        private val TAG = "PlayActivity"
        val DEVICESERIAL = "DEVICESERIAL"
        val VERIFYCODE = "VERIFYCODE"
        val CAMERANO = "CAMERANO"


        /**
         * 开启预览播放
         *
         * @param context
         */
        fun startPlayActivity(context: Context, deviceSerial: String, verifyCode: String, cameraNo: Int) {
            val intent = Intent(context, PlayActivity::class.java)
            intent.putExtra(DEVICESERIAL, deviceSerial)
            intent.putExtra(VERIFYCODE, verifyCode)
            intent.putExtra(CAMERANO, cameraNo)
            context.startActivity(intent)
        }
    }
}