package com.kindergartens.android.kindergartens.ext

import android.widget.EditText
import android.widget.TextView

/**
 * Created by zhangruiyu on 2017/6/28.
 */
fun TextView?.toText() = if (this == null) {
    ""
} else {
    text.trim().toString()
}

fun EditText?.canNotEdit() {
    this?.isFocusableInTouchMode = false
    this?.isFocusable = false
}

fun EditText?.canEdit() {
    this?.isFocusableInTouchMode = true
    this?.isFocusable = true
    this?.requestFocus()
}