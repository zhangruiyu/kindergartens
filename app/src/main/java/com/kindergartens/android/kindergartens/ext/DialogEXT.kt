package com.kindergartens.android.kindergartens.ext

import android.app.Dialog

/**
 * Created by zhangruiyu on 2017/7/26.
 */
fun Dialog?.safeDissmiss() {
    if (this != null && isShowing) {
        dismiss()
    }
}