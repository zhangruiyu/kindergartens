package com.kindergartens.okrxkotlin

/**
 * Created by zhangruiyu on 2017/7/12.
 */

class JsonSplit {
    companion object {
        fun split(old: String): String {
            val head = "data\":"
            val end = "\"msg\":"
//            val str = "body:{\"data\":{\"accessToken\":\"at.56s4x2b5ckpnn8n054kxbns0bwxxqw6k-7hxghe34wp-0mulil7-u5j8kclwj\",\"expireTime\":1500462624153},\"code\":\"200\",\"msg\":\"操作成功!\"}"
            var s = old.split(head)[1].split(end)[0]
            val lastIndexOf = s.lastIndexOf(",")
            s = s.substring(0, lastIndexOf)
            return s
        }
    }
}
