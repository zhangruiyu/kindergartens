package com.kindergartens.android.kindergartens.net

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
        val baseUrl = ""
       /* inline fun <reified T> getAuthCode(tel: String): Observable<T> {
            val request = OkGo.post<T>("${baseUrl}https://open.ys7.com/api/lapp/token/get")
            val params = HttpParams()
            params.put("tel", tel)
            request.params(params)

            return converter(request)
        }
        */
       inline fun <reified T> getYSToken(): Observable<T> {
           val request = OkGo.post<T>("${baseUrl}https://open.ys7.com/api/lapp/token/get")
           val params = HttpParams()
           params.put("appKey","b109fdee59b14b19b48927f627814c58")
           params.put("appSecret","fa7d8a8c75176be997d80f13590dfaa6")
           request.params(params)

           return converter(request)
       }
        inline fun <reified T> registerUser(tel: String, password: String, authCode: String): Observable<T> {
            val request = OkGo.post<T>("${baseUrl}https://open.ys7.com/api/lapp/token/get")
            val params = HttpParams()
            params.put("tel", tel)
            params.put("password", password)
            params.put("authCode", authCode)
            request.params(params)

            return converter(request)
        }

        inline fun <reified T> converter(request: PostRequest<T>): Observable<T> {
            return request.converter(JsonConvert(T::class.java)).adapt(ObservableBody<T>()).composeMain()
        }

    }
}