package android.kindergartens.com.core.modular.schoolmessage

import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.classroommessage.data.MessageEntity
import android.kindergartens.com.core.ui.CustomItemDecoration
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.trello.rxlifecycle2.android.ActivityEvent
import kotlinx.android.synthetic.main.activity_school_message.*
import org.jetbrains.anko.ctx

class SchoolMessageActivity : BaseToolbarActivity() {
    lateinit var schoolAdapter: SchoolAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_message)
        rv_school_message.layoutManager = LinearLayoutManager(ctx)
        schoolAdapter = SchoolAdapter()
        rv_school_message.addItemDecoration(CustomItemDecoration(ctx))
        rv_school_message.adapter = schoolAdapter
        bswr_school_message.setOnRefreshListener {
            getData()
        }
        bswr_school_message.autoRefresh()
        getData()
    }

    fun getData() {
        ServerApi.getSchoolMessage().doOnTerminate { bswr_school_message.finishRefresh() }.compose(this.bindUntilEvent(ActivityEvent.DESTROY)).subscribe(object : CustomNetErrorWrapper<MessageEntity>() {
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

