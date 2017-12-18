package android.kindergartens.com.custom.ui

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.kindergartens.com.R
import android.kindergartens.com.core.modular.classroom.data.ClassroomEntity
import android.os.Handler
import android.os.Message
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ezvizuikit.open.EZUIPlayer
import com.freedom.lauzy.playpauseviewlib.PlayPauseView
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer
import com.videogo.util.LogUtil
import kotlinx.android.synthetic.main.ui_camera_play.view.*
import org.jetbrains.anko.find
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by zhangruiyu on 2017/8/21.
 */
class CameraPlayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.ui_camera_play, this)
        mpb_loading.visibility = View.INVISIBLE
//        iv_play.setImageResource(R.drawable.ic_pause_circle_outline_white_18dp)
    }

    private val TAG = "CameraPlayView"
    var deviceInfo: ClassroomEntity.WrapperData.Data.KgCamera? = null
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mDefaultWidth: Int = 0
    private var mDefaultHeight: Int = 0
    var mStatus = 0
    val STATUS_INIT = 0
    val STATUS_START = 1
    val STATUS_STOP = 2
    val STATUS_PLAY = 3
    val STATUS_PAUSE = 4
    val eZOpenSDK = EZOpenSDK.getInstance()
    var mEzUIPlayerCallBack: EZUIPlayer.EZUIPlayerCallBack? = null
    var mEZPlayer: EZPlayer? = null
    private var mHolder: SurfaceHolder? = null
    private val isSurfaceInit = AtomicBoolean(false)
    private var mHandler: Handler

    init {
        class PlayResultHandler : Handler() {
            override fun handleMessage(msg: Message) {
                if ((context as Activity).isFinishing) {
                    return
                }
                val e: String?
                LogUtils.e(TAG, msg.what.toString())
                LogUtils.e(msg)
                when (msg.what) {
                    EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS, EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_SUCCUSS -> {
                        LogUtils.e(TAG, "MSG_REALPLAY_PLAY_SUCCESS")
                        dismissLoading()
//                optionSound()
                        if (mStatus != STATUS_STOP) {
                            mStatus = STATUS_PLAY
                            mEzUIPlayerCallBack?.onPlaySuccess()
                        }

                        sendEmptyMessage(8888)
                    }
                    EZConstants.MSG_VIDEO_SIZE_CHANGED -> {
                        LogUtils.e(TAG, "MSG_VIDEO_SIZE_CHANGED")
                        dismissLoading()

                        try {
                            e = msg.obj as String
                            val strings = e.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                            mEzUIPlayerCallBack?.onVideoSizeChange(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]))
                            mVideoWidth = Integer.parseInt(strings[0])
                            mVideoHeight = Integer.parseInt(strings[1])
                            setSurfaceSize(mWidth, mHeight)
                        } catch (var4: Exception) {
                            var4.printStackTrace()
                        }

                    }
                    EZConstants.EZRealPlayConstants.MSG_REALPLAY_STOP_SUCCESS -> {
                        showStopUi()
                    }
                    EZConstants.EZRealPlayConstants.MSG_REALPLAY_ENCRYPT_PASSWORD_ERROR -> {
                        stopPlay()
                        Toast.makeText(context, "安全密码错误,请联系幼儿园", Toast.LENGTH_SHORT).show()
                    }
                    EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL -> {
                        stopPlay()
                        Toast.makeText(context, "播放失败,请联系幼儿园", Toast.LENGTH_SHORT).show()
                    }
                    EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_FINISH -> {
                        LogUtils.d("EZUIPlayer", "MSG_REMOTEPLAYBACK_PLAY_FINISH")
//                        this@EZUIPlayer.handlePlayFinish()
                    }
                }
            }
        }

        mHandler = PlayResultHandler()

    }

    fun setParameters(deviceInfo: ClassroomEntity.WrapperData.Data.KgCamera, classroomImage: String) {
        this.deviceInfo = deviceInfo
        mEZPlayer?.let {
            it.release()
            mEZPlayer = null
        }
        mEZPlayer = eZOpenSDK.createPlayer(deviceInfo.deviceSerial, 1)
        mEZPlayer?.setSurfaceHold(mHolder)
        mEZPlayer?.setHandler(mHandler)
        Glide.with(context).load(classroomImage).apply(RequestOptions().centerCrop()).into(find(R.id.iv_classroom_image))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {


        this.mDefaultWidth = MeasureSpec.getSize(widthMeasureSpec)
        this.mDefaultHeight = MeasureSpec.getSize(heightMeasureSpec)
        val layoutParams = this.layoutParams
        if (layoutParams.height == -2) {
            this.mDefaultHeight = (this.mDefaultWidth.toDouble() * proportion).toInt()
        }

        LogUtil.d("EZUIPlayer", "onMeasure  mDefaultWidth = " + this.mDefaultWidth + "  mDefaultHeight= " + this.mDefaultHeight)
        val widthMeasureSpecP = MeasureSpec.makeMeasureSpec(this.mDefaultWidth, MeasureSpec.getMode(widthMeasureSpec))
        val heightMeasureSpecP = MeasureSpec.makeMeasureSpec(this.mDefaultHeight, MeasureSpec.getMode(heightMeasureSpec))
        if (sfv_camera_play != null && this.mHolder == null) {
            this.mHolder = sfv_camera_play.holder
            this.mHolder?.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    if (this@CameraPlayView.mEZPlayer != null) {
                        this@CameraPlayView.mEZPlayer?.setSurfaceHold(holder)
                        this@CameraPlayView.mEZPlayer?.setHandler(mHandler)
                    }

                    this@CameraPlayView.mHolder = holder
                    if (!this@CameraPlayView.isSurfaceInit.getAndSet(true)) {
//                        this@CameraPlayView.startPlay()
                    }

                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    if (mEZPlayer != null) {
                        mEZPlayer!!.setSurfaceHold(null)
                    }

                }
            })
        }
        play_pause_view.setPlayPauseListener(object : PlayPauseView.PlayPauseListener {
            override fun pause() {
                stopPlay()
            }

            override fun play() {
                startPlay()
            }

        })
        sfv_camera_play.setOnClickListener {
            stopPlay()
        }
        super.onMeasure(widthMeasureSpecP, heightMeasureSpecP)
    }

    fun startPlay() {
        if (mStatus != STATUS_START && mStatus != STATUS_PLAY) {
            if (mEZPlayer == null) {
                LogUtils.e("EZUIPlayer", "EZPlayer is null ,you can transfer createUIPlayer function")
            } else {
                if (isSurfaceInit.get()) {
                    mStatus = STATUS_START
                    showLoading()
                    if (deviceInfo?.isEncrypt == 1 && !deviceInfo?.verifyCode.isNullOrEmpty()) {
                        mEZPlayer?.setPlayVerifyCode(deviceInfo!!.verifyCode)
                    }
                    mEZPlayer?.startRealPlay()
                }
            }
        }
    }

    fun stopPlay() {
        this.mHandler.removeMessages(8888)
        if (this.mStatus != STATUS_STOP) {
            mStatus = STATUS_STOP
            this.mEZPlayer?.stopRealPlay()
        }
    }

    fun pausePlay() {
        LogUtils.d("EZUIPlayer", "pausePlay")
        this.mHandler.removeMessages(8888)
        this.mStatus = STATUS_PAUSE
        if (this.mEZPlayer != null) {
            this.mEZPlayer?.pausePlayback()
        }

    }

    fun releasePlayer() {
        this.mHandler.removeMessages(8888)
        if (this.mEZPlayer != null) {
            this.mEZPlayer?.release()
            this.mEZPlayer = null
        }

    }

    private fun showLoading() {
        TransitionManager.beginDelayedTransition(fl_camera_play, Fade(Fade.IN))
        mpb_loading.visibility = View.VISIBLE
        iv_classroom_image.visibility = View.INVISIBLE
        play_pause_view.visibility = View.VISIBLE

    }

    private fun dismissLoading() {
        TransitionManager.beginDelayedTransition(fl_camera_play, Fade(Fade.IN))
        mpb_loading.visibility = View.INVISIBLE
        play_pause_view.visibility = View.INVISIBLE
        iv_classroom_image.visibility = View.INVISIBLE
    }

    private fun showStopUi() {
        TransitionManager.beginDelayedTransition(fl_camera_play, Fade(Fade.IN))
        mpb_loading.visibility = View.INVISIBLE
        play_pause_view.visibility = View.VISIBLE
        play_pause_view.pause()
        iv_classroom_image.visibility = View.VISIBLE
    }

    fun setSurfaceSize(width: Int, height: Int) {
        this.mWidth = width
        this.mHeight = height
        var lp: android.view.ViewGroup.LayoutParams? = this.layoutParams
        if (lp == null) {
            lp = android.view.ViewGroup.LayoutParams(width, height)
        } else if (this.mWidth == 0 && this.mHeight == 0) {
            this.mWidth = lp.width
            this.mHeight = lp.height
            if (lp.width == -1) {
                this.mWidth = this.measuredWidth
            }

            if (lp.height == -1) {
                this.mHeight = this.measuredHeight
            }
        } else {
            lp.width = width
            lp.height = height
        }

        Log.d("EZUIPlayer", "setSurfaceSize  mWidth = " + this.mWidth + "  mHeight = " + this.mHeight)
        if (width == 0 && height != 0) {
            if (this.mVideoHeight != 0 && this.mVideoWidth != 0) {
                lp.width = ((height * this.mVideoWidth).toFloat() * 1.0f / this.mVideoWidth.toFloat()).toInt()
            } else {
                lp.width = (height.toDouble() * 1.1778).toInt()
            }
        }

        if (width != 0 && height == 0) {
            if (this.mVideoHeight != 0 && this.mVideoWidth != 0) {
                lp.height = ((width * this.mVideoHeight).toFloat() * 1.0f / this.mVideoWidth.toFloat()).toInt()
            } else {
                lp.height = (width.toDouble() * 0.562).toInt()
            }
        }

        TransitionManager.beginDelayedTransition(fl_camera_play, Fade(Fade.IN))
        this.layoutParams = lp
        try {
            val imageLayoutParams = iv_classroom_image.layoutParams
            imageLayoutParams.width = lp.width
            imageLayoutParams.height = lp.height
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.changeSurfaceSize(sfv_camera_play, this.mVideoWidth, this.mVideoHeight)
    }

    private fun changeSurfaceSize(surface: SurfaceView?, videoWidth: Int, videoHeight: Int) {
        if (surface != null) {
            val holder = surface.holder
            val size = this.getSurfaceSize(surface, videoWidth, videoHeight)
            if (size != null) {
                holder.setFixedSize(videoWidth, videoHeight)
                val lp = surface.layoutParams
                val oldH = lp.height
                lp.width = size!!.x
                lp.height = size!!.y
                Log.d("EZUIPlayer", "changeSurfaceSize  width =  " + lp.width + "  height = " + lp.height)
                surface.layoutParams = lp
            }
        }
    }

    private fun getSurfaceSize(surface: SurfaceView?, videoWidth: Int, videoHeight: Int): Point? {
        var videoWidth = videoWidth
        var videoHeight = videoHeight
        var pt: Point? = null
        if (surface == null) {
            return pt
        } else {
            if (videoWidth == 0 || videoHeight == 0) {
                videoWidth = 16
                videoHeight = 9
            }

            val sw = this.mWidth
            val sh = this.mHeight
            LogUtils.d("EZUIPlayer", "sw =  $sw  sh = $sh")
            var dw = sw.toDouble()
            var dh = sh.toDouble()
            if (dw * dh != 0.0 && videoWidth * videoHeight != 0) {
                val vw = videoWidth.toDouble()
                val ar = videoWidth.toDouble() / videoHeight.toDouble()
                val dar = dw / dh
                if (dar < ar) {
                    dh = dw / ar
                } else {
                    dw = dh * ar
                }

                val w = Math.ceil(dw * videoWidth.toDouble() / videoWidth.toDouble()).toInt()
                val h = Math.ceil(dh * videoHeight.toDouble() / videoHeight.toDouble()).toInt()
                pt = Point(w, h)
                Log.d("EZUIPlayer", "Point w=  $w  h = $h")
                return pt
            } else {
                return pt
            }
        }
    }

    companion object {
        val proportion: Double = 0.562
    }
}
//interface