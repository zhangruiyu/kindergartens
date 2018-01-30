package android.kindergartens.com.core.modular.tels

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.tels.data.TeacherEntity
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_tels.*
import org.jetbrains.anko.ctx

class TelsActivity : BaseToolbarActivity() {
    lateinit var teacherAdapter: TeacherAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tels)
        teacherAdapter = TeacherAdapter()
        val linearLayoutManager = LinearLayoutManager(ctx)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rcv_teachers.layoutManager = linearLayoutManager
        rcv_teachers.adapter = teacherAdapter
        ServerApi.getTels().subscribe(object : CustomNetErrorWrapper<TeacherEntity>() {
            override fun onNext(t: TeacherEntity) {

            }

        })
    }

    class TeacherAdapter : BaseQuickAdapter<TeacherEntity, BaseViewHolder>(R.layout.layout_tels_teacher_item) {
        override fun convert(helper: BaseViewHolder?, item: TeacherEntity) {

        }

    }
}
