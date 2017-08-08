package com.kindergartens.android.kindergartens.net

import com.kindergartens.android.kindergartens.core.modular.dynamic.data.DynamicSelectedPic
import com.kindergartens.android.kindergartens.core.modular.home.data.UserProfileEntity
import com.kindergartens.android.kindergartens.core.modular.userinfo.data.ProfileAlteredInfo
import com.kindergartens.android.kindergartens.core.tools.cos.data.SignInfo
import com.kindergartens.android.kindergartens.ext.composeMain
import com.kindergartens.okrxkotlin.JsonConvert
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import io.reactivex.Observable


/**
 * Created by zhangruiyu on 2017/6/28.
 */
class ServerApi {
    companion object {
        val baseUrl = "http://192.168.43.20:8080"
        /* inline fun <reified T> getAuthCode(tel: String): Observable<T> {
             val request = OkGo.post<T>("${baseUrl}https://open.ys7.com/api/lapp/token/get")
             val params = HttpParams()
             params.put("tel", tel)
             request.params(params)

             return converter(request)
         }
         */
        inline fun <reified T> getYSToken(): Observable<T> {
            val request = OkGo.post<T>("https://open.ys7.com/api/lapp/token/get")
            val params = HttpParams()
            params.put("appKey", "b109fdee59b14b19b48927f627814c58")
            params.put("appSecret", "fa7d8a8c75176be997d80f13590dfaa6")
            request.params(params)

            return converter(request)
        }

        inline fun <reified T> registerUser(tel: String, password: String, authCode: String): Observable<T> {
            val request = OkGo.post<T>("https://open.ys7.com/api/lapp/token/get")
            val params = HttpParams()
            params.put("tel", tel)
            params.put("password", password)
            params.put("authCode", authCode)
            request.params(params)

            return converter(request)
        }

        //多次sign
        fun getOCSPeriodEffectiveSignSign(type: Int): Observable<SignInfo> {
            val request = OkGo.post<SignInfo>("$baseUrl/user/cos/periodEffectiveSign")
            val params = HttpParams()
            params.put("type", type)
            request.params(params)

            return converter(request)
        }
        //单次sign
        fun getOCSOneEffectiveSignSign(type: Int): Observable<SignInfo> {
            val request = OkGo.post<SignInfo>("$baseUrl/user/cos/oneEffectiveSign")
            val params = HttpParams()
            params.put("type", type)
            request.params(params)

            return converter(request)
        }


        //发布视频动态
        fun commitDynamicVideo(dynamic_content: String, screenshot_server_url: String, video_server_url: String, video_long: String): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/user/dynamic/commitDynamicVideo")
            val params = HttpParams()
            params.put("type", 1)
            params.put("dynamic_content", dynamic_content)
            params.put("screenshot_server_url", screenshot_server_url)
            params.put("video_server_url", video_server_url)
            params.put("video_long", video_long)
            request.params(params)
            return converter(request)
        }

        //发布视频动态
        fun commitDynamicPic(dynamic_content: String, urls: ArrayList<DynamicSelectedPic.PicOrderInfo>): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/user/dynamic/commitDynamicPic")
            val params = HttpParams()
            params.put("type", 0)
            params.put("dynamic_content", dynamic_content)
            val customUrls = urls.joinToString { it.toString() }
            params.put("urls", customUrls)
            request.params(params)
            return converter(request)
        }

        //评论动态
        fun commitDynamicComment(commentContent: String, dynamicId: String, parentCommentId: String = "0", groupTag: String = ""): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/user/dynamic/commitComment")
            val params = HttpParams()
            params.put("commentContent", commentContent)
            params.put("dynamicId", dynamicId)
            params.put("parentCommentId", parentCommentId)
            params.put("groupTag", groupTag)
            request.params(params)
            return converter(request)
        }

        //评论动态
        fun getAccountProfile(): Observable<UserProfileEntity> {
            val request = OkGo.post<UserProfileEntity>("$baseUrl/user/profile")
            return converter(request)
        }

        //修改个人信息
        fun reviseProfile(checkGender: Int, relationCheck: Int, address: String, avatar: String): Observable<ProfileAlteredInfo> {
            val request = OkGo.post<ProfileAlteredInfo>("$baseUrl/user/reviseProfile")
            val params = HttpParams()
            params.put("checkGender", checkGender)
            params.put("relationCheck", relationCheck)
            params.put("address", address)
            params.put("avatarUrl", avatar)
            request.params(params)
            return converter(request)
        }

        inline fun <reified T> converter(request: PostRequest<T>): Observable<T> {
            return request.converter(JsonConvert(T::class.java)).adapt(ObservableBody<T>()).composeMain()
        }

    }
}