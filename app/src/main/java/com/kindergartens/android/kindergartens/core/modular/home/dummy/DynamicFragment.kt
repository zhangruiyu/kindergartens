package com.kindergartens.android.kindergartens.core.modular.home.dummy

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.database.SchoolmateHelper
import com.kindergartens.android.kindergartens.core.database.UserdataHelper
import com.kindergartens.android.kindergartens.core.modular.home.dummy.data.DynamicEntity
import com.kindergartens.android.kindergartens.core.ui.CustomLoadMoreView
import com.kindergartens.android.kindergartens.ext.*
import com.kindergartens.android.kindergartens.net.CustomNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import com.scwang.smartrefresh.header.PhoenixHeader
import com.yanyusong.y_divideritemdecoration.Y_Divider
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_dynamic.*
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast


/**
 * Created by zhangruiyu on 2017/7/13.
 */
class DynamicFragment : BaseFragment() {
    private var page_index: Int = 0
    private lateinit var dynamicAdapter: DynamicAdapter
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_dynamic, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        bsw_dynamic_refresh.setOnRefreshListener {
            refreshData()
        }
        bsw_dynamic_refresh.refreshHeader = PhoenixHeader(context)
        rcv_dynamic_content.addItemDecoration(DynamicItemDecoration(ctx))
        rcv_dynamic_content.layoutManager = LinearLayoutManager(ctx)
        dynamicAdapter = DynamicAdapter(ctx, childClickListener)
        dynamicAdapter.openLoadAnimation()
        val headView = View.inflate(ctx, R.layout.layout_dynamic_head, null)
        Glide.with(ctx).load("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=401967138,750679164&fm=26&gp=0.jpg")
                .override(width, (height / 6.5).toInt()).centerCrop().into(headView.find<ImageView>(R.id.iv_dynamic_head_pic))
        dynamicAdapter.addHeaderView(headView)
        dynamicAdapter.setOnLoadMoreListener({
            refreshData()
        }, rcv_dynamic_content)
        dynamicAdapter.setLoadMoreView(CustomLoadMoreView())
        dynamicAdapter.disableLoadMoreIfNotFullPage()
        dynamicAdapter.setEnableLoadMore(true)
        dynamicAdapter.setOnItemChildClickListener { adapter, view, position ->
            LogUtils.e("评论的动态位置====$position")

            when (view.id) {
                R.id.iv_reply -> {
                    MaterialDialog.Builder(ctx).titleColorRes(R.color.accent)
                            .title("请输入评论")
                            .inputRangeRes(5, 200, R.color.accent)
                            .input(null, null, { dialog, input ->
                                val waitDialog = ctx.getWaitDialog()
                                ServerApi.commitDynamicComment(input.toString(), view.tag as String)
                                        .doOnTerminate {
                                            waitDialog.safeDismiss()
                                            dialog.safeDismiss()
                                        }
                                        .subscribe(object : CustomNetErrorWrapper<Any>() {
                                            override fun onNext(t: Any) {
                                                adapter.data[position]?.let {
                                                    UserdataHelper.haveOnlineLet { onlineUser ->
                                                        val data = it as DynamicEntity.Data
                                                        data.tails.kgDynamicComment.add(DynamicEntity.Data.Tails.KgDynamicComment(input.toString(), onlineUser.id!!))
                                                        ctx.runOnUiThread {
                                                            //因为有头部 所有要pisition加1
                                                            adapter.notifyItemChanged(position + 1)
                                                        }
                                                    }
                                                }
                                            }
                                        })
                            }).show()
                }

            }
        }
        rcv_dynamic_content.adapter = dynamicAdapter
        super.onActivityCreated(savedInstanceState)
    }

    val childClickListener = { adapter: DynamicAdapter, view: View, position: Int ->
        when (view.tag) {
            is DynamicEntity.Data.Tails.KgDynamicComment -> {
                if ((view.tag as DynamicEntity.Data.Tails.KgDynamicComment).id == "0") {
                    toast("请刷新后在评论")
                } else {
                    MaterialDialog.Builder(ctx).titleColorRes(R.color.accent)
                            .title("请输入回复")
                            .inputRangeRes(5, 200, R.color.accent)
                            .input(null, null, { dialog, input ->
                                val waitDialog = ctx.getWaitDialog()
                                val tag = (view.tag as DynamicEntity.Data.Tails.KgDynamicComment)
                                ServerApi.commitDynamicComment(input.toString(), tag.dynamicId, tag.id, tag.groupTag)
                                        .doOnTerminate {
                                            waitDialog.safeDismiss()
                                        }
                                        .subscribe(object : CustomNetErrorWrapper<Any>() {
                                            override fun onNext(t: Any) {
                                                dialog.safeDismiss()
                                                adapter.data[position]?.let { dynamicEntity ->
                                                    UserdataHelper.haveOnlineLet { onlineUser ->
                                                        dynamicEntity.tails.kgDynamicComment.add(DynamicEntity.Data.Tails.KgDynamicComment(input.toString(), onlineUser.id!!, tag.groupTag, tag.id))
                                                        ctx.runOnUiThread {
                                                            //因为有头部 所有要pisition加1
                                                            adapter.notifyItemChanged(position + 1)
                                                        }
                                                    }
                                                }

                                            }

                                            override fun onError(e: Throwable) {
                                                super.onError(e)
                                            }
                                        })
                            }).show()
                }
            }
        }
        print("")
    }

    override fun onVisible() {
        super.onVisible()
        //初始化化
        page_index = 0
//        refreshData()
        bsw_dynamic_refresh.autoRefresh()
    }

    //获取动态
    private fun refreshData() {
        ServerApi.getDynamics(page_index).doOnTerminate { bsw_dynamic_refresh?.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<DynamicEntity>() {
            override fun onNext(it: DynamicEntity) {
                if (page_index == 0) {
                    dynamicAdapter.data.clear()
                    dynamicAdapter.notifyDataSetChanged()
                    rcv_dynamic_content.smoothScrollToPosition(0)
                }
                dynamicAdapter.addData(it.data.dynamics)
                val size = it.data.dynamics.size
                print(size)
                if (it.data.dynamics.size < 5) {
                    dynamicAdapter.loadMoreEnd()
                } else {
                    dynamicAdapter.loadMoreComplete()
                }
                page_index++
                if (it.data.allClassRoomUserInfo.size > 0) {
                    SchoolmateHelper.saveSchoolmates(it.data.allClassRoomUserInfo)
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                dynamicAdapter.loadMoreFail()
            }

        })

    }

}

class DynamicItemDecoration(val ctx: Context) : Y_DividerItemDecoration(ctx) {
    override fun getDivider(itemPosition: Int): Y_Divider {
        if (itemPosition == 0) {
            return Y_DividerBuilder().create()
        }
        return Y_DividerBuilder().setBottomSideLine(true, ctx.getColorSource(R.color.recycle_divider), 10f, 0f, 0f).create()
    }

}


