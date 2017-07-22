package com.kindergartens.android.kindergartens.core.modular.dynamic.data

import ch.halcyon.squareprogressbar.SquareProgressBar
import com.apkfuns.logutils.LogUtils
import com.kindergartens.android.kindergartens.base.BaseEntity
import com.kindergartens.android.kindergartens.core.ali.BizService
import com.kindergartens.android.kindergartens.core.tools.cos.COSTools
import com.kindergartens.android.kindergartens.custom.other.CustomUploadTaskListener
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
    var url: File? = null
    var resourceId: Int? = null
    var squareProgressBar: SquareProgressBar? = null
    var position: Int = 0
    var cosResult: PutObjectResult? = null
    var block: () -> Unit = {}
    var customUploadTaskListener: CustomUploadTaskListener = DynamicUploadTaskListener()

    constructor()
    constructor(url: File) {
        this.url = url
    }

    constructor(resourceId: Int) {
        this.resourceId = resourceId
    }

    fun putPicForDynamicSelectedPic(sign: String, cosPath: String) {
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
            this@DynamicSelectedPic.cosResult = cosResult as PutObjectResult
            block()
        }

        override fun onFailed(cosRequest: COSRequest, cosResult: COSResult) {
            super.onFailed(cosRequest, cosResult)
            val result = "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg
            LogUtils.d(result)
        }
    }
}