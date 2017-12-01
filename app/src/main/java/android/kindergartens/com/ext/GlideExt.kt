package android.kindergartens.com.ext

import android.content.Context
import android.kindergartens.com.R
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by zhangruiyu on 2017/6/22.
 */
fun ImageView.transitionImage(context: Context) {
    Glide.with(context).load(R.drawable.banner_normal)
}