package android.kindergartens.com.core.modular.dynamic.data

import android.kindergartens.com.base.BaseEntity
import android.kindergartens.com.core.ali.BizService
import android.kindergartens.com.core.tools.cos.COSTools
import android.kindergartens.com.custom.other.CustomUploadTaskListener
import ch.halcyon.squareprogressbar.SquareProgressBar
import com.apkfuns.logutils.LogUtils
import com.tencent.cos.model.COSRequest
import com.tencent.cos.model.COSResult
import com.tencent.cos.model.PutObjectResult
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import java.io.File

/**
 * Created by zhangruiyu on 2017/7/16.
 */
class DynamicSelectedPic : BaseEntity {
    //图片本地位置
    var url: File? = null
    //图片显示
    var resourceId: Int? = null
    //进度条
    var squareProgressBar: SquareProgressBar? = null
    //顺序
    var position: Int = 0
    //一共个数
    var size: Int? = null
    //上传后封装的info
    var uploadPics: ArrayList<PicOrderInfo>? = null
    var block: (DynamicSelectedPic) -> Unit = {}
    //回调
    var customUploadTaskListener: CustomUploadTaskListener = DynamicUploadTaskListener()
    var isSucceed = false

    constructor()
    constructor(url: File) {
        this.url = url
    }

    constructor(resourceId: Int) {
        this.resourceId = resourceId
    }

    @Synchronized
    fun addPics(picOrderInfo: PicOrderInfo) {
        uploadPics?.add(picOrderInfo)
    }

    fun putPicForDynamicSelectedPic(sign: String, cosPath: String, uploadPics: ArrayList<PicOrderInfo>, size: Int, block: (DynamicSelectedPic) -> Unit) {
        this.uploadPics = uploadPics
        this.block = block
        this.size = size
        val uploadRequest = COSTools.toUploadRequest(cosPath, url!!.absolutePath, sign)
        uploadRequest.listener = customUploadTaskListener
        val bizService = BizService.instance()
        squareProgressBar?.context?.doAsync {
            bizService.cosClient.putObject(uploadRequest)
        }
    }

    inner class DynamicUploadTaskListener : CustomUploadTaskListener() {
        override fun onProgress(cosRequest: COSRequest, currentSize: Long, totalSize: Long) {
            super.onProgress(cosRequest, currentSize, totalSize)
            val progress = (100.00 * currentSize / totalSize).toInt()
            squareProgressBar?.context?.runOnUiThread {
                squareProgressBar?.setProgress(progress)
            }
        }

        override fun onSuccess(cosRequest: COSRequest, cosResult: COSResult) {
            super.onSuccess(cosRequest, cosResult)
            val result = cosResult as PutObjectResult
            addPics(PicOrderInfo(result.resource_path, position))
            if (uploadPics?.size == size) {
                //上传完毕
                uploadPics!!.sortBy {
                    it.sequence
                }
                isSucceed = true
                block(this@DynamicSelectedPic)
            }

        }

        override fun onFailed(cosRequest: COSRequest, cosResult: COSResult) {
            super.onFailed(cosRequest, cosResult)
            val result = "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg
            if (isSucceed) {
                isSucceed = false
                block(this@DynamicSelectedPic)
            }
            LogUtils.d(result)
        }
    }

    class PicOrderInfo(val path: String, val sequence: Int) : BaseEntity {
        override fun toString(): String {
            return "$path*$sequence"
        }
    }

}