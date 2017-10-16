package com.kindergartens.android.kindergartens.core.modular.home.dummy.data

import com.mazouri.tools.Tools

/**
 * Created by zhangruiyu on 2017/7/26.
 */
class DynamicEntity(var data: WrapperDynamic) {
    data class WrapperDynamic(var allClassRoomUserInfo: ArrayList<DynamicProfile>, var dynamics: ArrayList<Data>)
    data class Data(
            var id: String,
            var content: String,
            var createTime: String,
            var nickName: String,
            var dynamicType: Int,
            var tails: Tails,
            var userId: Int) {
        data class Tails(/*var kgDynamicLiked: List<?>,*/
                var kgDynamicPics: List<KgDynamicPics>,
                var kgDynamicComment: ArrayList<KgDynamicComment>,
                var kgDynamicVideo: KgDynamicVideo

        ) {
            data class KgDynamicPics(var dynamicId: Int,
                                     var id: Int,
                                     var picUrl: String,
                                     var sequence: Int)

            data class KgDynamicVideo(var videoLength: String,
                                      var videoUrl: String,
                                      var videoPic: String)

            data class KgDynamicComment(var id: String, var userId: String, var dynamicId: String, var commentContent: String,
                                        var createTime: String, var groupTag: String, var parentCommentId: String) {
                //回复动态最顶层评论
                constructor(commentContent: String, userId: String) :
                        this("0", userId, "0", commentContent, Tools.time().nowTimeString, Tools.time().nowTimeMills.toString(), "0")

                constructor(commentContent: String, userId: String, groupTag: String, parentCommentId: String) :
                        this("0", userId, "0", commentContent, "", groupTag, parentCommentId)
            }
        }
    }


    data class DynamicProfile(var userId: String, var nickName: String)
}
