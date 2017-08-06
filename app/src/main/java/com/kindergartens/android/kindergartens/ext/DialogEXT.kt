package com.kindergartens.android.kindergartens.ext

import android.app.Dialog
import android.content.Context
import android.view.KeyEvent
import com.afollestad.materialdialogs.MaterialDialog
import com.kindergartens.android.kindergartens.R

/**
 * Created by zhangruiyu on 2017/7/26.
 */
fun Dialog?.safeDismiss() {
    if (this != null && isShowing) {
        dismiss()
    }
}

fun Dialog?.setUnCancel(): Dialog {
    this!!.setCancelable(false)
    this.setOnKeyListener { _, keyCode, _ ->
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_SEARCH) {
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
    return this
}

fun Context?.getWaitDialog(title: String = "正在发布中"): MaterialDialog {
    return MaterialDialog.Builder(this!!)
            .title(title)
            .content(R.string.please_wait)
            .progress(true, 0)
            .show()
}