package com.kindergartens.android.kindergartens.core.modular.home

import am.widget.stateframelayout.StateFrameLayout
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.modular.home.data.DynamicEntiy
import com.kindergartens.okrxkotlin.http
import kotlinx.android.synthetic.main.fragment_dynamic.*
import org.jetbrains.anko.debug
import org.jetbrains.anko.support.v4.toast

/**
 * Created by zhangruiyu on 2017/7/13.
 */
class DynamicFragment : BaseFragment() {
    private var page_index: Int = 0
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_dynamic, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        bsw_dynamic_refresh.setOnRefreshListener {
            toast("加载")
//            bsw_dynamic_refresh.handler.postDelayed({ bsw_dynamic_refresh.isRefreshing = false }, 1500)
            bsw_dynamic_refresh.isRefreshing = false
        }
        sfl_dynamic_content_false.state = StateFrameLayout.STATE_LOADING
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
        http<ArrayList<DynamicEntiy>> {
            url = "/user/dynamic/getSchoolMessage"
            params = mapOf("page_index" to page_index)
            onSuccess {
                debug { it }
            }
            doOnTerminate {
                bsw_dynamic_refresh?.isRefreshing = false
            }
        }

    }

}