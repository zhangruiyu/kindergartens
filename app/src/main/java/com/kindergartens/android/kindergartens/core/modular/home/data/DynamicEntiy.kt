package com.kindergartens.android.kindergartens.core.modular.home.data

/**
 * Created by zhangruiyu on 2017/7/13.
 */
data class DynamicEntiy(var content: String,
                        var createTime: String,
                        var tails: Tails,
                        var userId: Int,
                        var nickName: String) {
    data class Tails(var kgDynamicLiked: List<KgDynamicLiked>,
                     var kgDynamicPics: List<KgDynamicPics>) {
        data class KgDynamicLiked(var nickName: String,
                                  var userId: String)

        data class KgDynamicPics(var dynamicId: Int,
                                 var id: Int,
                                 var picUrl: String,
                                 var sequence: Int)
    }
}