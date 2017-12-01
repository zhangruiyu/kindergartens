package android.kindergartens.com.core.modular.home.dummy.data

import android.kindergartens.com.base.BaseEntity
import com.mazouri.tools.Tools

/**
 * Created by zhangruiyu on 2017/10/4.
 */
data class CommentEntity(var code: Int,
                            var msg: String,
                            var data: KgDynamicComment) : BaseEntity {
    data class KgDynamicComment(var id: String, var userId: String, var dynamicId: String, var commentContent: String,
                                var createTime: String, var groupTag: String, var parentCommentId: String) {
        //回复动态最顶层评论
        constructor(commentContent: String, userId: String) :
                this("0", userId, "0", commentContent, Tools.time().nowTimeString, Tools.time().nowTimeMills.toString(), "0")

        constructor(commentContent: String, userId: String, groupTag: String, parentCommentId: String) :
                this("0", userId, "0", commentContent, "", groupTag, parentCommentId)
    }
}