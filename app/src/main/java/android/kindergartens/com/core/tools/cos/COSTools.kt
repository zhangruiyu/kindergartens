package android.kindergartens.com.core.tools.cos

import android.kindergartens.com.core.ali.BizService
import com.mazouri.tools.io.FileTool
import com.tencent.cos.model.PutObjectRequest

/**
 * Created by zhangruiyu on 2017/7/18.
 */
class COSTools {
    companion object {

        /**
         * 简单文件上传 : <20M的文件，直接上传
         */
        fun toUploadRequest(cosPath: String, localPath: String, sign: String): PutObjectRequest {
            val bizService = BizService.instance()
            /** PutObjectRequest 请求对象  */
            val putObjectRequest = PutObjectRequest()
            /** 设置Bucket  */
            putObjectRequest.bucket = bizService.bucket
            /** 设置cosPath :远程路径 */
            val fileExtension = FileTool.instance().getFileName(localPath)
            putObjectRequest.cosPath = "$cosPath/$fileExtension"
            /** 设置srcPath: 本地文件的路径  */
            putObjectRequest.srcPath = localPath
            /** 设置 insertOnly: 是否上传覆盖同名文件 */
            putObjectRequest.insertOnly = "1"
            /** 设置sign: 签名，此处使用多次签名  */
            putObjectRequest.sign = sign
            //        putObjectRequest.setSign(bizService.getSign());
            /** 设置sha: 是否上传文件时带上sha，一般不需要带 */
            //putObjectRequest.setSha(putObjectRequest.getsha());
            return putObjectRequest
            /** 设置listener: 结果回调  */
            //        putObjectRequest.listener = customUploadTaskListener
            /** 发送请求：执行  */
            //        bizService.cosClient.putObject(putObjectRequest)
        }
    }
}