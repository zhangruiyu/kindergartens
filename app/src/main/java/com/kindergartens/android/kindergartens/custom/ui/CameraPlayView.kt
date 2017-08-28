package com.kindergartens.android.kindergartens.custom.ui

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ezvizuikit.open.EZUIPlayer
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.ext.getWidth
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer
import com.videogo.openapi.bean.EZCameraInfo
import com.videogo.openapi.bean.EZDeviceInfo
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
    var deviceInfo: EZDeviceInfo? = null
    private var cameraInfo: EZCameraInfo? = null
    private var verifyCode: String? = null
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private var mWidth = 0
    private var mHeight = 0
    var mStatus = 0
    val STATUS_INIT = 0
    val STATUS_START = 1
    val STATUS_STOP = 2
    val STATUS_PLAY = 3
    val STATUS_PAUSE = 4
    val eZOpenSDK = EZOpenSDK.getInstance()
    var mEzUIPlayerCallBack: EZUIPlayer.EZUIPlayerCallBack? = null
    private var mEZPlayer: EZPlayer? = null
    private var mHolder: SurfaceHolder? = null
    private val isSurfaceInit = AtomicBoolean(false)
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if ((context as Activity).isFinishing) {
                return
            }
            val e: String?
            LogUtil.e(TAG, msg.what.toString())
            when (msg.what) {
                EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS, EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_SUCCUSS -> {
                    LogUtil.e(TAG, "MSG_REALPLAY_PLAY_SUCCESS")
                    dismissLoading()
//                optionSound()
                    if (mStatus != STATUS_STOP) {
                        mStatus = STATUS_PLAY
                        mEzUIPlayerCallBack?.onPlaySuccess()
                    }

                    sendEmptyMessage(8888)
                }
                EZConstants.MSG_VIDEO_SIZE_CHANGED -> {
                    LogUtil.e(TAG, "MSG_VIDEO_SIZE_CHANGED")
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
                EZConstants.EZRealPlayConstants.MSG_REALPLAY_ENCRYPT_PASSWORD_ERROR -> {
                    Toast.makeText(context, "安全密码错误,请联系幼儿园", Toast.LENGTH_SHORT).show()
                }
                EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_FAIL -> {
                    Toast.makeText(context, "播放失败,请联系幼儿园", Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
    }


    fun setParameters(deviceInfo: EZDeviceInfo, cameraInfoFromDevice: EZCameraInfo, verifyCode: String, classroomImage: String) {
        this.deviceInfo = deviceInfo
        this.cameraInfo = cameraInfoFromDevice
        this.verifyCode = verifyCode
        mEZPlayer?.let {
            it.release()
            mEZPlayer = null
        }
        mEZPlayer = EZOpenSDK.getInstance().createPlayer(cameraInfoFromDevice.deviceSerial, cameraInfoFromDevice.cameraNo)
        mEZPlayer?.setSurfaceHold(mHolder)
        mEZPlayer?.setHandler(mHandler)
        iv_classroom_image.setBackgroundResource(R.drawable.dynamic_selected_add)
        Glide.with(context).load(classroomImage).into(find<ImageView>(R.id.iv_classroom_image))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        val mDefaultHeight = (context.getWidth().toDouble() * 0.562).toInt()

        LogUtil.d("EZUIPlayer", "onMeasure  mDefaultWidth = " + context.getWidth() + "  mDefaultHeight= " + mDefaultHeight)
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(context.getWidth(), MeasureSpec.getMode(widthMeasureSpec))
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mDefaultHeight, MeasureSpec.getMode(heightMeasureSpec))
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
        iv_play.setOnClickListener {
            startPlay()
        }
        sfv_camera_play.setOnClickListener {
            stopPlay()
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun startPlay() {
        if (mStatus != STATUS_START && mStatus != STATUS_PLAY) {
            if (mEZPlayer == null) {
                LogUtil.e("EZUIPlayer", "EZPlayer is null ,you can transfer createUIPlayer function")
            } else {
                if (isSurfaceInit.get()) {
                    mStatus = STATUS_START
                    showLoading()
                    if (deviceInfo?.isEncrypt == 1 && !verifyCode.isNullOrEmpty()) {
                        mEZPlayer?.setPlayVerifyCode(verifyCode)
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
            showStopUi()
        }
    }

    fun pausePlay() {
        LogUtil.debugLog("EZUIPlayer", "pausePlay")
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
        mpb_loading.visibility = View.VISIBLE
        iv_classroom_image.visibility = View.VISIBLE
        iv_play.visibility = View.INVISIBLE

    }

    private fun dismissLoading() {
        mpb_loading.visibility = View.INVISIBLE
        iv_play.visibility = View.INVISIBLE
        iv_classroom_image.visibility = View.VISIBLE
    }

    private fun showStopUi() {
        mpb_loading.visibility = View.INVISIBLE
        iv_play.visibility = View.VISIBLE
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

        this.layoutParams = lp
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
            LogUtil.d("EZUIPlayer", "sw =  $sw  sh = $sh")
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
}
//interface