package com.kindergartens.android.kindergartens.core.modular.eat

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.modular.eat.data.EatEntity
import com.kindergartens.android.kindergartens.net.CustomNetErrorWrapper
import com.kindergartens.android.kindergartens.net.ServerApi
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_eat.*
import moe.feng.common.stepperview.IStepperAdapter
import moe.feng.common.stepperview.VerticalStepperItemView
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.ctx
/**
 * Created by zhangruiyu on 2017/9/22.
 */
class EatFragment : BaseFragment(), IStepperAdapter, OnRefreshListener {
    private var position: Int = 0
    private val data = ArrayList<EatEntity.Data>()
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_eat, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        srl_refresh.setOnRefreshListener(this)
    }

    override fun getTitle(index: Int): CharSequence {
//        return TimeUtil.getWeekOfDate(data[index].createTime)
        return data[index].breakfast.trim() + "   " + data[index].lunch.trim() + "   " + data[index].supper.trim()
    }

    //应该显示图片
    override fun onCreateCustomView(index: Int, context: Context?, parent: VerticalStepperItemView?): View {
        val inflateView = LayoutInflater.from(context).inflate(R.layout.fragment_eat_adapter_item, parent, false);
        val contentView = inflateView.findViewById<ImageView>(R.id.item_content);
        Glide.with(ctx).load("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=809943721,3434341815&fm=27&gp=0.jpg").apply(bitmapTransform(CircleCrop()))
                .into(contentView)

        Glide.with(ctx).load("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1903235548,795322350&fm=200&gp=0.jpg").apply(bitmapTransform(CircleCrop()))
                .into(inflateView.find(R.id.item_content2))
        Glide.with(ctx).load("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1669158391,872664803&fm=27&gp=0.jpg").apply(bitmapTransform(CircleCrop()))
                .into(inflateView.find(R.id.item_content3))
        val nextButton = inflateView.findViewById<Button>(R.id.button_next)
        nextButton.text = if (index == size() - 1) "确认" else "下一步"
        nextButton.setOnClickListener({
            if (!vertical_stepper_view.nextStep()) {
                vertical_stepper_view.setErrorText(0, if (vertical_stepper_view.getErrorText(0) == null) "Test error" else null)
                Snackbar.make(vertical_stepper_view, "设置错误信息!", Snackbar.LENGTH_LONG).show()
            }
        })
        val prevButton = inflateView.findViewById<Button>(R.id.button_prev)
        prevButton.text = if (index == 0) "上一步" else "上一步"
        inflateView.findViewById<Button>(R.id.button_prev).setOnClickListener({
            if (index != 0) {
                vertical_stepper_view.prevStep()
            } else {
                vertical_stepper_view.isAnimationEnabled = !vertical_stepper_view.isAnimationEnabled
            }
        })
        return inflateView
    }

    override fun getSummary(index: Int): CharSequence? = null

    override fun size(): Int = data.size

    override fun onShow(p0: Int) {
    }

    override fun onHide(p0: Int) {
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(): EatFragment = EatFragment()
    }

    fun refreshData(position: Int) {
        this.position = position;
        srl_refresh.autoRefresh()

    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        ServerApi.getEatInfoList(position).doOnTerminate { refreshLayout.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<EatEntity>() {
            override fun onNext(t: EatEntity) {
                data.clear()
                data.addAll(t.data)
                data.sortBy { it.createTime }
                vertical_stepper_view.stepperAdapter = this@EatFragment
            }

        })
    }
}