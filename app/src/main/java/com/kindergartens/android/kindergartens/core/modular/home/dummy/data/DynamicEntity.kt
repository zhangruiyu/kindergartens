package com.kindergartens.android.kindergartens.core.modular.home.dummy.data

/**
 * Created by zhangruiyu on 2017/7/26.
 */
class DynamicEntity(var data: ArrayList<Data>) {
    data class Data(var content: String,
                    var createTime: String,
                    var nickName: String,
                    var tails: Tails,
                    var userId: Int) {
        data class Tails(/*var kgDynamicLiked: List<?>,*/
                var kgDynamicPics: List<KgDynamicPics>,
                var kgDynamicComment: List<KgDynamicComment>) {
            data class KgDynamicPics(var dynamicId: Int,
                                     var id: Int,
                                     var picUrl: String,
                                     var sequence: Int)

            data class KgDynamicComment(var id: Long, var userId: Long, var dynamicId: Long, var commentContent: String,
                                        var createTime: String, var groupTag: String, var parentCommentId: String)
        }
    }
}