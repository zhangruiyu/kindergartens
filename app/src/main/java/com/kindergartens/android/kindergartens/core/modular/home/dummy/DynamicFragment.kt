package com.kindergartens.android.kindergartens.core.modular.home.dummy

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.modular.home.dummy.data.DynamicEntity
import com.kindergartens.android.kindergartens.core.ui.CustomLoadMoreView
import com.kindergartens.android.kindergartens.ext.getColorSource
import com.kindergartens.android.kindergartens.ext.height
import com.kindergartens.android.kindergartens.ext.width
import com.kindergartens.okrxkotlin.http
import com.yanyusong.y_divideritemdecoration.Y_Divider
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_dynamic.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.ctx

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
            onVisible()
        }
        rcv_dynamic_content.addItemDecoration(DynamicItemDecoration(ctx))
        rcv_dynamic_content.layoutManager = LinearLayoutManager(ctx)
        dynamicAdapter = DynamicAdapter(ctx)
        dynamicAdapter.openLoadAnimation()
        val headView = View.inflate(ctx, R.layout.layout_dynamic_head, null)
        Glide.with(ctx).load("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=401967138,750679164&fm=26&gp=0.jpg")
                .override(width, (height / 5.5).toInt()).centerCrop().into(headView.find<ImageView>(R.id.iv_dynamic_head_pic))
        dynamicAdapter.addHeaderView(headView)
        dynamicAdapter.setOnLoadMoreListener({
            refreshData()
        }, rcv_dynamic_content)
        dynamicAdapter.setLoadMoreView(CustomLoadMoreView())
        dynamicAdapter.disableLoadMoreIfNotFullPage()
        dynamicAdapter.setEnableLoadMore(true)
        rcv_dynamic_content.adapter = dynamicAdapter
        super.onActivityCreated(savedInstanceState)
    }

    override fun onVisible() {
        super.onVisible()
        //初始化化
        page_index = 0
        refreshData()
        bsw_dynamic_refresh.isRefreshing = true
    }

    //获取动态
    private fun refreshData() {
        http<DynamicEntity> {
            url = "/user/dynamic/list"
            params = mapOf("page_index" to page_index)
            onSuccess {
                if (page_index == 0) {
                    dynamicAdapter.data.clear()
                    dynamicAdapter.notifyDataSetChanged()
                    rcv_dynamic_content.smoothScrollToPosition(0)
                }
                dynamicAdapter.addData(it.data)
                val size = it.data.size
                print(size)
                if (it.data.size < 5){
                    dynamicAdapter.loadMoreEnd()
                }else{
                    dynamicAdapter.loadMoreComplete()
                }
                page_index++
            }
            doOnTerminate {
                bsw_dynamic_refresh?.isRefreshing = false
            }
            onFail {
                dynamicAdapter.loadMoreFail()
            }
        }

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


