@file:Suppress("DEPRECATION")

package com.kindergartens.android.kindergartens.ext

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.Fragment
import com.kindergartens.android.kindergartens.KGApplication


/**
 * Created by zhangruiyu on 2017/6/22.
 */
//获取屏幕高度   lazy只加载一次
val width: Int by lazy {
    KGApplication.kgApplication.resources.displayMetrics.widthPixels
}
val height: Int by lazy {
    KGApplication.kgApplication.resources.displayMetrics.heightPixels
}

fun Context.width() = width
fun Context.height() = height

fun Fragment.getColorSource(@ColorRes id: Int) = resources.getColor(id)
fun Context.getColorSource(@ColorRes id: Int) = resources.getColor(id)

fun Fragment.getDrawableSource(@DrawableRes id: Int) = resources.getDrawable(id)
fun Context.getDrawableSource(@DrawableRes id: Int) = resources.getDrawable(id)