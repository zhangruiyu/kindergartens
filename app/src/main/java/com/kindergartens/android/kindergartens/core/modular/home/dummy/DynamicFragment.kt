package com.kindergartens.android.kindergartens.core.modular.home.dummy

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.find
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast


/**
 * Created by zhangruiyu on 2017/7/13.
 */
class DynamicFragment : BaseFragment() {
    private var page_index: Int = 0
    private var dynamicAdapter: DynamicAdapter? = null
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
        val headView = View.inflate(ctx, R.layout.layout_dynamic_head, null)

        Glide.with(ctx).load("http://img5.imgtn.bdimg.com/it/u=561855009,461918882&fm=27&gp=0.jpg").apply(RequestOptions()
                .override(width, (height / 6.5).toInt()).centerCrop())
                .into(headView.find(R.id.iv_dynamic_head_pic))

        dynamicAdapter = DynamicAdapter(ctx, childClickListener)
        dynamicAdapter?.apply {
            openLoadAnimation()
            addHeaderView(headView)
            setOnLoadMoreListener({
                refreshData()
            }, rcv_dynamic_content)
            setLoadMoreView(CustomLoadMoreView())
            disableLoadMoreIfNotFullPage()
            setEnableLoadMore(true)
            setOnItemChildClickListener { adapter, view, position ->
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
                                                            data.kgDynamicComment.add(DynamicEntity.KgDynamicComment(input.toString(), onlineUser.id!!))
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
        }

        rcv_dynamic_content.adapter = dynamicAdapter
        //刷新时不大幅度闪烁
        (rcv_dynamic_content.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        super.onActivityCreated(savedInstanceState)
//        initData()
    }

    private val childClickListener = { adapter: DynamicAdapter, view: View, position: Int ->
        when (view.tag) {
            is DynamicEntity.KgDynamicComment -> {
                if ((view.tag as DynamicEntity.KgDynamicComment).id == "0") {
                    toast("请刷新后在评论")
                } else {
                    MaterialDialog.Builder(ctx).titleColorRes(R.color.accent)
                            .title("请输入回复")
                            .inputRangeRes(5, 200, R.color.accent)
                            .input(null, null, { dialog, input ->
                                val waitDialog = ctx.getWaitDialog()
                                val tag = (view.tag as DynamicEntity.KgDynamicComment)
                                ServerApi.commitDynamicComment(input.toString(), tag.dynamicId, tag.id, tag.groupTag)
                                        .doOnTerminate {
                                            waitDialog.safeDismiss()
                                        }
                                        .subscribe(object : CustomNetErrorWrapper<Any>() {
                                            override fun onNext(t: Any) {
                                                dialog.safeDismiss()
                                                adapter.data[position]?.let { dynamicEntity ->
                                                    UserdataHelper.haveOnlineLet { onlineUser ->
                                                        dynamicEntity.kgDynamicComment.add(DynamicEntity.KgDynamicComment(input.toString(), onlineUser.id!!, tag.groupTag, tag.id))
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
    }

    private fun firstGetData() = runBlocking {
        //<Unit>可略, 开始主协程
        launch(CommonPool) {
            delay(200L)
            if (bsw_dynamic_refresh != null) {
                bsw_dynamic_refresh.autoRefresh()
            } else {
                refreshData()
            }
        }
    }

    fun initData() {
        //初始化
        page_index = 0
        if (dynamicAdapter == null || dynamicAdapter?.data?.size == 0) {
            firstGetData()
        } else {
            refreshData()
        }
    }

    //获取动态
    private fun refreshData() {
        ServerApi.getDynamics(page_index).doOnTerminate { bsw_dynamic_refresh?.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<DynamicEntity>() {
            override fun onNext(it: DynamicEntity) {
                if (page_index == 0) {
                    dynamicAdapter?.data?.clear()
                    dynamicAdapter?.notifyDataSetChanged()
                    //跳转到第一个
//                    rcv_dynamic_content.smoothScrollToPosition(0)
                }
                dynamicAdapter?.addData(it.data.dynamics)
                val size = it.data.dynamics.size
                print(size)
                if (it.data.dynamics.size < 5) {
                    dynamicAdapter?.loadMoreEnd()
                } else {
                    dynamicAdapter?.loadMoreComplete()
                }
                page_index++
                if (it.data.allClassRoomUserInfo.size > 0) {
                    SchoolmateHelper.saveSchoolmates(it.data.allClassRoomUserInfo)
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                dynamicAdapter?.loadMoreFail()
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


