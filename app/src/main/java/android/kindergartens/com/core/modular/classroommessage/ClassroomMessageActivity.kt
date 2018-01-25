package android.kindergartens.com.core.modular.classroommessage

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.classroommessage.data.MessageEntity
import android.kindergartens.com.core.ui.CustomItemDecoration
import android.kindergartens.com.ext.TSnackbarUtils
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View.GONE
import android.view.View.VISIBLE
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.trello.rxlifecycle2.android.ActivityEvent
import kotlinx.android.synthetic.main.activity_classroom_message.*
import org.jetbrains.anko.act
import org.jetbrains.anko.ctx


class ClassroomMessageActivity : BaseToolbarActivity() {
    lateinit var schoolAdapter: SchoolAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classroom_message)
        rv_school_message.layoutManager = LinearLayoutManager(ctx)
        schoolAdapter = SchoolAdapter()
        rv_school_message.addItemDecoration(CustomItemDecoration(ctx))
        rv_school_message.adapter = schoolAdapter
        bswr_school_message.setOnRefreshListener {
            getData()
        }
        bswr_school_message.autoRefresh()
        getData()

        UserdataHelper.haveOnlineLet {
            if (it.roleCode != null && it.roleCode!!.toInt() < 1) {
                fab.visibility = GONE
            } else {
                fab.visibility = VISIBLE
                fab.setOnClickListener {
                    MaterialDialog.Builder(this)
                            .title("请输入消息内容").titleColorRes(R.color.accent)
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .inputRangeRes(5, 30, R.color.error_hint_color)
                            .input("消息内容", "", { dialog, input ->
                                ServerApi.commitMessage(input.toString()).subscribe(object : CustomNetErrorWrapper<Any>() {
                                    override fun onNext(t: Any) {
                                        TSnackbarUtils.toSuccess(act, "消息发步成功")
                                        getData()
                                    }

                                })
                            }).show()

                }
            }
        }

    }

    fun getData() {
        ServerApi.getClassroomMessage().compose(this.bindUntilEvent(ActivityEvent.DESTROY)).doOnTerminate { bswr_school_message.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<MessageEntity>() {
            override fun onNext(t: MessageEntity) {
                schoolAdapter.setNewData(t.data)
            }

        })
    }

    class SchoolAdapter : BaseQuickAdapter<MessageEntity.Data, BaseViewHolder>(R.layout.layout_item_school_message) {
        override fun convert(helper: BaseViewHolder, item: MessageEntity.Data) {
            helper.setText(R.id.actv_content, item.message)
                    .setText(R.id.actv_create_time, item.createTime)

        }

    }
}

