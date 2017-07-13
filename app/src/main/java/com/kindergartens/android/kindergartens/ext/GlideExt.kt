package com.kindergartens.android.kindergartens.ext

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kindergartens.android.kindergartens.R

/**
 * Created by zhangruiyu on 2017/6/22.
 */
fun ImageView.transitionImage(context: Context) {
    Glide.with(context).load(R.drawable.banner_normal)
}