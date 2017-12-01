package android.kindergartens.com.core.modular.dynamic.data

import android.kindergartens.com.core.ali.BizService
import android.kindergartens.com.core.tools.cos.COSTools
import android.kindergartens.com.custom.other.CustomUploadTaskListener
import ch.halcyon.squareprogressbar.SquareProgressBar
import com.apkfuns.logutils.LogUtils
import com.tencent.cos.model.COSRequest
import com.tencent.cos.model.COSResult
import com.tencent.cos.model.PutObjectResult
import org.jetbrains.anko.runOnUiThread

/**
 * Created by zhangruiyu on 2017/7/23.
 */
class VideoUpload(var video_screenshot: String, var video_uri: String, var block: (VideoUpload) -> Unit) {
    lateinit var video_server_url: String
    lateinit var screenshot_server_url: String
    var isSucceed = false
    //9秒
    val video_long: String = "9"
    var squareProgressBar: SquareProgressBar? = null
    var count = 0
    fun putPicForDynamicSelectedPic(sign: String, cosPath: String) {
        val bizService = BizService.instance()
        val uploadPicRequest = COSTools.toUploadRequest(cosPath, video_screenshot, sign)
        uploadPicRequest.listener = DynamicUploadTaskListener(true)
        bizService.cosClient.putObjectAsyn(uploadPicRequest)

        val uploadVideoRequest = COSTools.toUploadRequest(cosPath, video_uri, sign)
        uploadVideoRequest.listener = DynamicUploadTaskListener(false)
        bizService.cosClient.putObjectAsyn(uploadVideoRequest)

    }


    inner class DynamicUploadTaskListener(val isPic: Boolean) : CustomUploadTaskListener() {
        override fun onProgress(cosRequest: COSRequest, currentSize: Long, totalSize: Long) {
            super.onProgress(cosRequest, currentSize, totalSize)
            val progress = (100.00 * currentSize / totalSize).toInt()
            squareProgressBar?.context?.runOnUiThread {
                squareProgressBar?.setProgress(progress)
            }
        }

        override fun onSuccess(cosRequest: COSRequest, cosResult: COSResult) {
            super.onSuccess(cosRequest, cosResult)
            val putObjectResult = cosResult as PutObjectResult
            if (isPic) {
                screenshot_server_url = putObjectResult.resource_path
            } else {
                video_server_url = putObjectResult.resource_path
            }
            count++
            if (count == 2) {
                isSucceed = true
                block(this@VideoUpload)
            }
        }

        override fun onFailed(cosRequest: COSRequest, cosResult: COSResult) {
            super.onFailed(cosRequest, cosResult)
            if (isSucceed) {
                isSucceed = false
                block(this@VideoUpload)
            }
            val result = "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg
            LogUtils.d(result)
        }
    }
}