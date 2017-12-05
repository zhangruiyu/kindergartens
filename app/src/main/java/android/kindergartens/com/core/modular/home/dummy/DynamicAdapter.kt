package android.kindergartens.com.core.modular.home.dummy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.kindergartens.com.R
import android.kindergartens.com.core.database.SchoolmateHelper
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.auth.LoginActivity
import android.kindergartens.com.core.modular.home.dummy.data.DynamicEntity
import android.kindergartens.com.core.modular.video.TCConstants
import android.kindergartens.com.core.modular.video.preview.TCVideoPreviewActivity
import android.kindergartens.com.core.tools.CustomTimeUtil
import android.kindergartens.com.ext.getWidth
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.iwf.photopicker.PhotoPreview
import org.jetbrains.anko.dimen
import org.jetbrains.anko.startActivity

/**
 * Created by zhangruiyu on 2017/7/26.
 */
class DynamicAdapter(val ctx: Context, val childClick: (DynamicAdapter, View, Int) -> Unit) : BaseQuickAdapter<DynamicEntity.Data, BaseViewHolder>(R.layout.layout_item_dynamic) {

    override fun convert(helper: BaseViewHolder, item: DynamicEntity.Data) {
        Glide.with(ctx).load(R.drawable.ic_face_primary_24dp).apply(bitmapTransform(CircleCrop())).into(helper.getView<ImageView>(R.id.iv_dynamic_head_pic))
        helper.setText(R.id.tv_dynamic_create_time, CustomTimeUtil.getTimeFormatText(item.createTime))
                .setText(R.id.tv_dynamic_content, item.content)
                .setText(R.id.tv_dynamic_nick_name, item.nickName).addOnClickListener(R.id.iv_reply).addOnClickListener(R.id.iv_share).addOnClickListener(R.id.iv_liked)
                .setTag(R.id.iv_reply, item.id)


        helper.getView<View>(R.id.iv_liked)?.setOnClickListener {
            if (UserdataHelper.getOnlineUser() == null) {
                ctx.startActivity<LoginActivity>()
            } else {
                ServerApi.commitDynamicLiked(item.id).subscribe(object : CustomNetErrorWrapper<Any>() {
                    override fun onNext(t: Any) {}
                })
                item.kgDynamicLiked.add(UserdataHelper.getOnlineUser()!!.id!!)
                refreshLiked(helper, item)
            }
        }

        refreshLiked(helper, item)

        //设置图片start
        val fl_dynamic_video = helper.getView<View>(R.id.fl_dynamic_video)
        val recyclerView = helper.getView<RecyclerView>(R.id.rcy_dynamic_pic)
        //1是视频
        if (item.dynamicType == 1) {
            fl_dynamic_video.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            Glide.with(ctx).load(item.kgDynamicVideo.videoPic).into(helper.getView<ImageView>(R.id.iv_video_image))
            helper.getView<ImageView>(R.id.iv_video_image).setOnClickListener {
                val intent = Intent(ctx, TCVideoPreviewActivity::class.java)
                intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH, item.kgDynamicVideo.videoUrl)
                intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, item.kgDynamicVideo.videoPic)
                intent.putExtra(TCConstants.VIDEO_RECORD_DURATION, item.kgDynamicVideo.videoLength)
                ctx.startActivity(intent)
            }

        } else {
            fl_dynamic_video.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            setUpDynamicImage(recyclerView, item, helper)
        }
        //设置图片end
        //设置评论
        setUpComment(helper, item)
    }

    private fun refreshLiked(helper: BaseViewHolder, item: DynamicEntity.Data) {
        if (UserdataHelper.getOnlineUser() == null) {
            helper.getView<View>(R.id.iv_liked).isEnabled = true
        } else {
            helper.getView<View>(R.id.iv_liked).isEnabled = item.kgDynamicLiked.contains(UserdataHelper.getOnlineUser()!!.id) != true
        }
        if (item.kgDynamicLiked.size > 0) {
            SchoolmateHelper.getALlSchoolmateAndRun { data ->
                helper.setText(R.id.tv_liked, item.kgDynamicLiked.fold(StringBuffer(), { total, next ->
                    total.append(data[next] + "、")
                }))
            }
            helper.setGone(R.id.ll_liked, true)
        } else {
            helper.setGone(R.id.ll_liked, false)
        }
    }

    private fun setUpDynamicImage(recyclerView: RecyclerView, item: DynamicEntity.Data, helper: BaseViewHolder) {
        if (item.kgDynamicPics.isEmpty()) {
            return@setUpDynamicImage
        }
        val layoutParams = recyclerView.layoutParams
        val row: Int = if (item.kgDynamicPics.size % 3 != 0) {
            item.kgDynamicPics.size / 3 + 1
        } else {
            item.kgDynamicPics.size / 3
        }
        val item_width = ctx.getWidth() - ctx.dimen(R.dimen.item_normal_margin) * 2
        layoutParams.width = item_width
        LogUtils.e("动态图片row == $row")
        layoutParams.height = item_width / 3 * row
        val layoutManager = object : GridLayoutManager(ctx, if (item.kgDynamicPics.size > 2) 3 else item.kgDynamicPics.size) {}
        layoutManager.isAutoMeasureEnabled = true
        recyclerView.layoutManager = layoutManager
        //        rcv_pics.addItemDecoration(SpaceItemDecoration())

        if (recyclerView.adapter == null) {
            recyclerView.adapter = DynamicPicAdapter(ctx)
        }

        val adapter = recyclerView.adapter
        if (adapter is DynamicPicAdapter) {
            adapter.openLoadAnimation()
            adapter.setNewData(item.kgDynamicPics)
            adapter.setOnItemClickListener({ _, _, position ->
                val preImages = ArrayList<String>()
                preImages.addAll(item.kgDynamicPics.map { it.picUrl })
                PhotoPreview.builder()
                        .setPhotos(preImages)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(ctx as Activity)
            })
        }

    }

    private fun setUpComment(helper: BaseViewHolder, item: DynamicEntity.Data) {
        //设置评论start
        val commentLinearLayout = helper.getView<LinearLayout>(R.id.ll_dynamic_comment)
        commentLinearLayout.removeAllViews()
        val groupBy = item.kgDynamicComment.groupBy({
            //按照tag排序呢
            it.groupTag
        })
        LogUtils.e("分组后的集合有${groupBy.size}个数")
        groupBy.forEach { comments ->
            comments.value.forEachIndexed { index, kgDynamicComment ->
                val textView = TextView(ctx)
                if (index != 0) {
                    textView.setPadding(120, 0, 0, 0)
                } else {
                    textView.setPadding(0, 0, 0, 0)
                }
                SchoolmateHelper.getNickName(kgDynamicComment.userId, { nickName ->
                    val style = SpannableStringBuilder("$nickName:${kgDynamicComment.commentContent}")
                    style.setSpan(ForegroundColorSpan(Color.GRAY), 0, nickName.length + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    style.setSpan(ForegroundColorSpan(Color.BLACK), nickName.length, style.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    textView.text = style
                })
                textView.tag = kgDynamicComment
                commentLinearLayout.addView(textView)
                textView.setOnClickListener { childClick(this@DynamicAdapter, textView, helper.layoutPosition - headerLayoutCount) }
            }
        }
    }


    //设置评论end
}


class DynamicPicAdapter(val ctx: Context) : BaseQuickAdapter<DynamicEntity.KgDynamicPics, BaseViewHolder>(R.layout.layout_item_dynamic_pic) {
    override fun convert(helper: BaseViewHolder, item: DynamicEntity.KgDynamicPics) {
        val imageView = helper.getView<ImageView>(R.id.iv_dynamic_item_pic)
        Glide.with(ctx).load(item.picUrl).into(imageView)
        val layoutParams = imageView.layoutParams
        layoutParams.width = ctx.getWidth() / 3
        layoutParams.height = ctx.getWidth() / 3
    }

}
