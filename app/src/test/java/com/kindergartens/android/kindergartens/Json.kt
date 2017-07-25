package com.kindergartens.android.kindergartens

import com.kindergartens.android.kindergartens.core.modular.dynamic.data.DynamicSelectedPic
import org.junit.Test

/**
 * Created by zhangruiyu on 2017/7/12.
 */
class Json {
    @Test
    fun testJson(){
        val arrayListOf = arrayListOf(DynamicSelectedPic.PicOrderInfo("1", 5), DynamicSelectedPic.PicOrderInfo("1", 2), DynamicSelectedPic.PicOrderInfo("1", 3), DynamicSelectedPic.PicOrderInfo("1", 0))
        arrayListOf.sortBy{
            it.sequence
        }
//        Collections.sort(arrayListOf, DynamicSelectedPic.PicOrderInfoComparator())
        val arrayListOf1 = arrayListOf
    }
}