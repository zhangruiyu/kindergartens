package com.kindergartens.android.kindergartens.net

import com.kindergartens.android.kindergartens.core.modular.album.data.AlbumEntity
import com.kindergartens.android.kindergartens.core.modular.auth.data.LoginUserEntity
import com.kindergartens.android.kindergartens.core.modular.classroom.data.ClassroomEntity
import com.kindergartens.android.kindergartens.core.modular.classroom.data.YSToken
import com.kindergartens.android.kindergartens.core.modular.classroom.data.YsHelper
import com.kindergartens.android.kindergartens.core.modular.dynamic.data.DynamicSelectedPic
import com.kindergartens.android.kindergartens.core.modular.eat.data.EatEntity
import com.kindergartens.android.kindergartens.core.modular.home.data.UserProfileEntity
import com.kindergartens.android.kindergartens.core.modular.home.dummy.data.CommentEntity
import com.kindergartens.android.kindergartens.core.modular.home.dummy.data.DynamicEntity
import com.kindergartens.android.kindergartens.core.modular.schoolmessage.data.MessageEntity
import com.kindergartens.android.kindergartens.core.modular.userinfo.data.ProfileAlteredInfo
import com.kindergartens.android.kindergartens.core.tools.cos.data.SignInfo
import com.kindergartens.android.kindergartens.ext.composeMain
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.HttpParams
import com.lzy.okgo.request.PostRequest
import com.lzy.okrx2.adapter.ObservableBody
import io.reactivex.Observable
import java.util.*


/**
 * Created by zhangruiyu on 2017/6/28.
 */
class ServerApi {
    companion object {
        //手机
//        val baseUrl = "http://192.168.43.20:8080"
        val baseUrl = "http://192.168.31.150:8080"
        /* inline fun <reified T> getAuthCode(tel: String): Observable<T> {
             val request = OkGo.post<T>("${baseUrl}https://open.ys7.com/api/lapp/token/get")
             val params = HttpParams()
             params.put("tel", tel)
             request.params(params)

             return converter(request)
         }
         */

        inline fun <reified T> converter(request: PostRequest<T>): Observable<T> =
                request.converter(JsonConvert(T::class.java)).adapt(ObservableBody<T>()).composeMain()


        inline fun <reified T> registerUser(tel: String, password: String, authCode: String): Observable<T> {
            val request = OkGo.post<T>("https://open.ys7.com/api/lapp/token/get")
            val params = HttpParams()
            params.put("tel", tel)
            params.put("password", password)
            params.put("authCode", authCode)
            request.params(params)

            return converter(request)
        }

        fun changePassword(oldPassword: String, newPassword: String): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/user/auth/changePassword")
            val params = HttpParams()
            params.put("oldPassword", oldPassword)
            params.put("newPassword", newPassword)
            request.params(params)

            return converter(request)
        }

        //获取萤石token
        fun getYSToken(): Observable<YSToken> {
            val localToken = YsHelper.getLocalToken()
            return if (localToken.isNullOrEmpty()) {
                converter(OkGo.post<YSToken>("$baseUrl/user/ys/registerAndGenerateToken")).map {
                    YsHelper.saveYSToken(it.data.accessToken!!)
                    it
                }
            } else {
                Observable.just(YSToken(200, YSToken.Data(localToken!!), ""))
            }
        }

        //获取萤石token
        fun geLocationYSToken(): String? = YsHelper.getLocalToken()

        fun getClassrooms(): Observable<ClassroomEntity> {
            val request = OkGo.post<ClassroomEntity>("$baseUrl/user/ys/classroom/list")
            val params = HttpParams()
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

        //登陆
        fun login(tel: String, password: String): Observable<LoginUserEntity> {
            val request = OkGo.post<LoginUserEntity>("$baseUrl/public/auth/login")
            val params = HttpParams()
            params.put("tel", tel)
            params.put("password", password)
            request.params(params)
            return converter(request)
        }

        //获取动态
        fun getDynamics(page_index: Int): Observable<DynamicEntity> {
            val request = OkGo.post<DynamicEntity>("$baseUrl/user/dynamic/list")
            val params = HttpParams()
            params.put("page_index", page_index)
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
        fun commitDynamicComment(commentContent: String, dynamicId: String, parentCommentId: String = "0", groupTag: String = ""): Observable<CommentEntity> {
            val request = OkGo.post<CommentEntity>("$baseUrl/user/dynamic/commitComment")
            val params = HttpParams()
            params.put("commentContent", commentContent)
            params.put("dynamicId", dynamicId)
            params.put("parentCommentId", parentCommentId)
            params.put("groupTag", groupTag)
            request.params(params)
            return converter(request)
        }

        fun commitDynamicLiked(dynamicId: String): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/user/dynamic/commitLiked")
            val params = HttpParams()
            params.put("dynamicId", dynamicId)
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


        //校园消息
        fun getSchoolMessage(): Observable<MessageEntity> {
            val request = OkGo.post<MessageEntity>("$baseUrl/user/messageList/schoolMessage")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }


        //饮食信息
        fun getEatInfoList(index: Int = 0): Observable<EatEntity> {
            val request = OkGo.post<EatEntity>("$baseUrl/user/eat/eatList")
            val params = HttpParams()
            params.put("index", index)
            request.params(params)
            return converter(request)
        }

        fun getSchoolAlbum(): Observable<AlbumEntity> {
            val request = OkGo.post<AlbumEntity>("$baseUrl/user/album/schoolAlbum")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }


    }
}