package com.kindergartens.android.kindergartens.net

import java.io.Serializable

/**
 * Created by zhangruiyu on 2017/6/28.
 */

open class LzyResponse : Serializable {

    var code: Int = 0
    var msg: String = ""

    override fun toString(): String {
        return "LzyResponse{\n" + //

                "\tcode=" + code + "\n" + //

                "\tmsg='" + msg + "\'\n" + //


                '}'
    }

    companion object {

        private const val serialVersionUID = 5213230387175987834L
    }
}