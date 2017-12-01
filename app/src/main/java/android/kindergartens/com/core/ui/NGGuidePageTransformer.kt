package com.kindergartens.android.kindergartens.core.ui

import android.support.v4.view.ViewPager
import android.view.View

/**
 * Created by zhangruiyu on 2017/6/22.
 */

class NGGuidePageTransformer : ViewPager.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width    //得到view宽

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left. 出了左边屏幕
            view.alpha = 0f

        } else if (position <= 1) { // [-1,1]
            if (position < 0) {
                //消失的页面
                view.translationX = -pageWidth * position  //阻止消失页面的滑动
            } else {
                //出现的页面
                view.translationX = pageWidth.toFloat()        //直接设置出现的页面到底
                view.translationX = -pageWidth * position  //阻止出现页面的滑动
            }
            // Fade the page relative to its size.
            val alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position))
            //透明度改变Log
            view.alpha = alphaFactor
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.    出了右边屏幕
            view.alpha = 0f
        }
    }

    companion object {
        private val MIN_ALPHA = 0.0f    //最小透明度
    }


}