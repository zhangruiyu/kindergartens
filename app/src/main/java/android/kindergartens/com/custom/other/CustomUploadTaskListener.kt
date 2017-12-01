package android.kindergartens.com.custom.other

import com.apkfuns.logutils.LogUtils
import com.tencent.cos.model.COSRequest
import com.tencent.cos.model.COSResult
import com.tencent.cos.model.PutObjectResult
import com.tencent.cos.task.listener.IUploadTaskListener

/**
 * Created by zhangruiyu on 2017/7/20.
 */
open class CustomUploadTaskListener : IUploadTaskListener {
    override fun onProgress(cosRequest: COSRequest, currentSize: Long, totalSize: Long) {
        val progress = (100.00 * currentSize / totalSize).toLong()
        LogUtils.e("progress =$progress%")
    }

    override fun onCancel(cosRequest: COSRequest, cosResult: COSResult) {
        val result = "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg
        LogUtils.e(result)
    }

    override fun onSuccess(cosRequest: COSRequest, cosResult: COSResult) {
        val putObjectResult = cosResult as PutObjectResult
        val stringBuilder = StringBuilder()
        stringBuilder.append(" 上传结果： ret=" + putObjectResult.code + "; msg =" + putObjectResult.msg + "\n")
        stringBuilder.append(if (" access_url= " + putObjectResult.access_url == null) "null" else putObjectResult.access_url + "\n")
        stringBuilder.append(if (" resource_path= " + putObjectResult.resource_path == null) "null" else putObjectResult.resource_path + "\n")
        stringBuilder.append(if (" url= " + putObjectResult.url == null) "null" else putObjectResult.url)
        val result = stringBuilder.toString()
        LogUtils.e(result)
    }

    override fun onFailed(cosRequest: COSRequest, cosResult: COSResult) {
        val result = "上传出错： ret =" + cosResult.code + "; msg =" + cosResult.msg
        LogUtils.e(result)
    }
}