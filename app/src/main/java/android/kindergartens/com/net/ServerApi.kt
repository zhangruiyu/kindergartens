package android.kindergartens.com.net

import android.kindergartens.com.Constants
import android.kindergartens.com.core.modular.album.data.AlbumEntity
import android.kindergartens.com.core.modular.auth.data.CodeWrapperEntity
import android.kindergartens.com.core.modular.auth.data.LoginUserEntity
import android.kindergartens.com.core.modular.cameralist.ClassroomEntity
import android.kindergartens.com.core.modular.cameralist.YSToken
import android.kindergartens.com.core.modular.cameralist.YsHelper
import android.kindergartens.com.core.modular.classroommessage.data.MessageEntity
import android.kindergartens.com.core.modular.dynamic.data.DynamicSelectedPic
import android.kindergartens.com.core.modular.eat.data.EatEntity
import android.kindergartens.com.core.modular.home.data.BannerEntity
import android.kindergartens.com.core.modular.home.data.SchoolEntity
import android.kindergartens.com.core.modular.home.data.UserProfileEntity
import android.kindergartens.com.core.modular.home.dummy.data.CommentEntity
import android.kindergartens.com.core.modular.home.dummy.data.DynamicEntity
import android.kindergartens.com.core.modular.tels.data.TeacherEntity
import android.kindergartens.com.core.modular.userinfo.data.ProfileAlteredInfo
import android.kindergartens.com.core.tools.cos.data.SignInfo
import android.kindergartens.com.ext.composeMain
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
//        文浩
//        val baseUrl = "http://192.168.253.14:8080"
        val baseUrl = "http://192.168.31.150:8080"
        /* inline fun <reified T> getAuthCode(tel: String): Observable<T> {
             val request = OkGo.post<T>("${baseUrl}https://open.ys7.com/api/lapp/token/get")
             val params = HttpParams()
             params.put("tel", tel)
             request.params(params)

             return converter(request)
         }
         */
        const val USER_URL = "/user/normal"
        const val TEACHER_URL = "/user/teacher"

        inline fun <reified T> converter(request: PostRequest<T>): Observable<T> =
                request.converter(JsonConvert(T::class.java)).adapt(ObservableBody<T>()).composeMain()


        fun changePassword(oldPassword: String, newPassword: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + USER_URL}/auth/changePassword")
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
                converter(OkGo.post<YSToken>("${baseUrl + USER_URL}/ys/registerAndGenerateToken")).map {
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
            val request = OkGo.post<ClassroomEntity>("${baseUrl + USER_URL}/ys/classroom/list")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }

        //多次sign
        fun getOCSPeriodEffectiveSignSign(type: Int): Observable<SignInfo> {
            val request = OkGo.post<SignInfo>("${baseUrl + USER_URL}/cos/periodEffectiveSign")
            val params = HttpParams()
            params.put("type", type)
            request.params(params)

            return converter(request)
        }

        //单次sign
        fun getOCSOneEffectiveSignSign(type: Int): Observable<SignInfo> {
            val request = OkGo.post<SignInfo>("${baseUrl + USER_URL}/cos/oneEffectiveSign")
            val params = HttpParams()
            params.put("type", type)
            request.params(params)

            return converter(request)
        }

        //登陆
        fun login(tel: String, password: String, pushToken: String): Observable<LoginUserEntity> {
            val request = OkGo.post<LoginUserEntity>("$baseUrl/public/auth/login")
            val params = HttpParams()
            params.put("tel", tel)
            params.put("password", password)
            params.put("pushToken", pushToken)
            request.params(params)
            return converter(request)
        }

        //发送注册验证码
        fun sendRegisterCode(tel: String): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/public/auth/register1")
            val params = HttpParams()
            params.put("tel", tel)
            request.params(params)
            return converter(request)
        }

        //发送注册验证码
        fun registerUser(tel: String, password: String, authCode: String): Observable<Any> {
            val request = OkGo.post<Any>("$baseUrl/public/auth/register2")
            val params = HttpParams()
            params.put("tel", tel)
            params.put("password", password)
            params.put("authCode", authCode)
            request.params(params)
            return converter(request)
        }

        //判断是否注册过
        fun verifyIsRegister(tel: String): Observable<CodeWrapperEntity> {
            val request = OkGo.post<CodeWrapperEntity>("$baseUrl/public/verifyIsRegister")
            val params = HttpParams()
            params.put("tel", tel)
            request.params(params)
            return converter(request)
        }

        //获取动态
        fun getDynamics(page_index: Int): Observable<DynamicEntity> {
            val request = OkGo.post<DynamicEntity>("${baseUrl + USER_URL}/dynamic/list")
            val params = HttpParams()
            params.put("page_index", page_index)
            request.params(params)
            return converter(request)
        }

        //发布视频动态
        fun commitDynamicVideo(dynamic_content: String, screenshot_server_url: String, video_server_url: String, video_long: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + USER_URL}/dynamic/commitDynamicVideo")
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
            val request = OkGo.post<Any>("${baseUrl + USER_URL}/dynamic/commitDynamicPic")
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
            val request = OkGo.post<CommentEntity>("${baseUrl + USER_URL}/dynamic/commitComment")
            val params = HttpParams()
            params.put("commentContent", commentContent)
            params.put("dynamicId", dynamicId)
            params.put("parentCommentId", parentCommentId)
            params.put("groupTag", groupTag)
            request.params(params)
            return converter(request)
        }

        fun commitDynamicLiked(dynamicId: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + USER_URL}/dynamic/commitLiked")
            val params = HttpParams()
            params.put("dynamicId", dynamicId)
            request.params(params)
            return converter(request)
        }

        //评论动态
        fun getAccountProfile(): Observable<UserProfileEntity> {
            val request = OkGo.post<UserProfileEntity>("${baseUrl + USER_URL}/profile")
            return converter(request)
        }

        //修改个人信息
        fun reviseProfile(checkGender: Int, relationCheck: Int, address: String, avatar: String): Observable<ProfileAlteredInfo> {
            val request = OkGo.post<ProfileAlteredInfo>("${baseUrl + USER_URL}/reviseProfile")
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
            val request = OkGo.post<MessageEntity>("${baseUrl + USER_URL}/messageList/schoolMessage")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }

        //班级消息
        fun getClassroomMessage(): Observable<MessageEntity> {
            val request = OkGo.post<MessageEntity>("${baseUrl + USER_URL}/messageList/classroomMessage")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }

        fun commitMessage(message: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + TEACHER_URL}/messageList/addClassroomMessage")
            val params = HttpParams()
            params.put("message", message)
            request.params(params)
            return converter(request)
        }

        //饮食信息
        fun getEatInfoList(date: String): Observable<EatEntity> {
            val request = OkGo.post<EatEntity>("${baseUrl + USER_URL}/eat/eatList")
            val params = HttpParams()
            params.put("date", date)
            request.params(params)
            return converter(request)
        }

        fun getSchoolAlbum(): Observable<AlbumEntity> {
            val request = OkGo.post<AlbumEntity>("${baseUrl + USER_URL}/album/schoolAlbum")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }

        fun getBanner(): Observable<BannerEntity> {
            val request = OkGo.post<BannerEntity>("${baseUrl}/canUserToken/getBanner")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }

        //编辑饮食信息
        fun commitEat(breakfast: String, lunch: String, supper: String, urls: String, date: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + TEACHER_URL}/eat/addEat")
            val params = HttpParams()
            params.put("breakfast", breakfast)
            params.put("lunch", lunch)
            params.put("supper", supper)
            params.put("urls", urls)
            params.put("date", date)
            request.params(params)
            return converter(request)
        }

        fun getSchoolInfo(schoolId: String): Observable<SchoolEntity> {
            val request = OkGo.post<SchoolEntity>("$baseUrl/canUserToken/schoolInfo")
            val params = HttpParams()
            params.put("schoolId", schoolId)
            request.params(params)
            return converter(request)

        }

        fun loginByQQWeixin(uid: String, name: String, gender: String, iconurl: String, platform: String): Observable<LoginUserEntity> {
            val request = OkGo.post<LoginUserEntity>("$baseUrl/public/qqWeixinLogin")
            val params = HttpParams()
            params.put("uid", uid)
            params.put("name", name)
            params.put("gender", gender)
            params.put("iconurl", iconurl)
            params.put("platform", platform)
            params.put("pushToken", Constants.PushToken)
            request.params(params)
            return converter(request)
        }

        fun unbindQQORWechat(platform: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + USER_URL}/unbindQQORWechat")
            val params = HttpParams()
            params.put("platform", platform)
            request.params(params)
            return converter(request)

        }

        fun bindQQORWechat(uid: String, nickName: String, gender: String, iconurl: String, platform: String): Observable<Any> {
            val request = OkGo.post<Any>("${baseUrl + USER_URL}/bindQQORWechat")
            val params = HttpParams()
            params.put("platform", platform)
            params.put("uid", uid)
            params.put("nickName", nickName)
            params.put("iconurl", iconurl)
            params.put("gender", gender)
            request.params(params)
            return converter(request)

        }

        fun getTels(): Observable<TeacherEntity> {
            val request = OkGo.post<TeacherEntity>("${baseUrl + TEACHER_URL}/teachersAndStudentsInfo")
            val params = HttpParams()
            request.params(params)
            return converter(request)
        }

    }
}