package android.kindergartens.com.core.modular.tels

import am.widget.stateframelayout.StateFrameLayout
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.tels.data.TeacherEntity
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.trello.rxlifecycle2.android.ActivityEvent
import kotlinx.android.synthetic.main.activity_tels.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast

class TelsActivity : BaseToolbarActivity() {
    lateinit var teacherAdapter: TeacherAdapter
    lateinit var studentAdapter: StudentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tels)
        teacherAdapter = TeacherAdapter()
        val linearLayoutManager = LinearLayoutManager(ctx)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rcv_teachers.layoutManager = linearLayoutManager
        rcv_teachers.adapter = teacherAdapter

        rcv_students.layoutManager = LinearLayoutManager(ctx)
        studentAdapter = StudentAdapter()
        rcv_students.adapter = studentAdapter
        submitbutton.setOnClickListener {
            toast("目前暂不可网上申请,请直接联系园长咨询")
            submitbutton.reset()
        }
        sfl_lyt_state.setOnStateClickListener(object : StateFrameLayout.OnAllStateClickListener {
            override fun onLoadingClick(layout: StateFrameLayout?) = Unit

            override fun onErrorClick(layout: StateFrameLayout?) {
                refreshData()
            }

            override fun onEmptyClick(layout: StateFrameLayout?) = Unit

            override fun onNormalClick(layout: StateFrameLayout?) = Unit

        })
        refreshData()
    }

    private fun refreshData() {
        ServerApi.getTels().compose(this.bindUntilEvent(ActivityEvent.DESTROY)).doOnTerminate { srl_refresh.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<TeacherEntity>() {
            override fun onNext(t: TeacherEntity) {
                teacherAdapter.setNewData(t.data.teachers)
                studentAdapter.setNewData(t.data.students)
                sfl_lyt_state.normal()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                sfl_lyt_state.error()
            }

        })
    }

    class TeacherAdapter : BaseQuickAdapter<TeacherEntity.Data.Teachers, BaseViewHolder>(R.layout.layout_tels_teacher_item) {
        override fun convert(helper: BaseViewHolder, item: TeacherEntity.Data.Teachers) {
            helper.setText(R.id.tv_teacher_name, item.realName)
                    .setText(R.id.tv_teacher_grade, item.showName + "教师")
            Glide.with(helper.itemView.context).load(item.avatar).apply(bitmapTransform(CircleCrop())).into(helper.getView(R.id.iv_teacher_pic))
        }

    }

    class StudentAdapter : BaseQuickAdapter<TeacherEntity.Data.Students, BaseViewHolder>(R.layout.layout_tels_student_item) {
        override fun convert(helper: BaseViewHolder, item: TeacherEntity.Data.Students) {
            helper.setText(R.id.tv_student_name, item.realName)
            Glide.with(helper.itemView.context).load(item.avatar).apply(bitmapTransform(CircleCrop())).into(helper.getView(R.id.iv_student_pic))
        }

    }
}
