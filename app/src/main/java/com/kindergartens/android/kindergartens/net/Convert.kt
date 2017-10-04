package com.kindergartens.android.kindergartens.net

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import java.io.Reader
import java.lang.reflect.Type

/**
 * Created by zhangruiyu on 2017/6/30.
 */

object Convert {

    fun create(): Gson {
        return Convert.GsonHolder.gson
    }

    private object GsonHolder {
        val gson = Gson()
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: String, type: Class<T>): T {
        return create().fromJson(json, type)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return create().fromJson<T>(json, type)
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: JsonReader, typeOfT: Type): T {
        return create().fromJson<T>(reader, typeOfT)
    }

    @Throws(JsonSyntaxException::class, JsonIOException::class)
    fun <T> fromJson(json: Reader, classOfT: Class<T>): T {
        return create().fromJson(json, classOfT)
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: Reader, typeOfT: Type): T {
        return create().fromJson<T>(json, typeOfT)
    }

    fun toJson(src: Any): String {
        return create().toJson(src)
    }

    fun toJson(src: Any, typeOfSrc: Type): String {
        return create().toJson(src, typeOfSrc)
    }

}