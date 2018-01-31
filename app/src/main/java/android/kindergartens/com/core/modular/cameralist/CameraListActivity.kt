package android.kindergartens.com.core.modular.cameralist

import am.widget.stateframelayout.StateFrameLayout
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.cameralplay.PlayActivity
import android.kindergartens.com.custom.ui.GlideRoundTransform
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.mazouri.tools.Tools
import com.trello.rxlifecycle2.android.ActivityEvent
import com.videogo.exception.BaseException
import com.videogo.openapi.EZOpenSDK
import kotlinx.android.synthetic.main.activity_camera_list.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast


class CameraListActivity : BaseToolbarActivity() {

    lateinit var cameraListAdapter: CameraListAdapter
    lateinit var layout_camera_list_footer: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_list)
        srl_refresh.setOnRefreshListener {
            refreshData()
        }
        sfl_lyt_state.setOnStateClickListener(object : StateFrameLayout.OnAllStateClickListener {
            override fun onLoadingClick(layout: StateFrameLayout?) = Unit

            override fun onErrorClick(layout: StateFrameLayout?) {
                refreshData()
            }

            override fun onEmptyClick(layout: StateFrameLayout?) = Unit

            override fun onNormalClick(layout: StateFrameLayout?) = Unit

        })
        rcv_camera_list.layoutManager = LinearLayoutManager(ctx)
//        rcv_camera_list.addItemDecoration(DynamicItemDecoration(ctx))
        cameraListAdapter = CameraListAdapter()
        cameraListAdapter.setOnItemClickListener { adapter, view, position ->
            val data = adapter.data[position] as ClassroomEntity.WrapperData.Data
            if (data.unWatch == 1) {
                val kgCamera = data.kgCamera
                if (kgCamera?.deviceSerial?.isNotEmpty() == true) {
                    PlayActivity.startPlayActivity(this, kgCamera.deviceSerial!!.trim(), kgCamera.verifyCode, 1)
                } else {
                    toast("此教室暂未开放在线摄像头")
                }
            } else {
                toast("未到开放时间,请下拉刷新后再次尝试")
            }

        }
        rcv_camera_list.itemAnimator.changeDuration = 0
        if (Tools.apk().isAppDebug(this)) {
            cameraListAdapter.setOnItemLongClickListener { adapter, view, position ->
                val data = adapter.data[position] as ClassroomEntity.WrapperData.Data
                data.unWatch = if (data.unWatch == 1) 0 else 1
                cameraListAdapter.notifyItemChanged(position)
                true
            }
        }
        rcv_camera_list.adapter = cameraListAdapter
        layout_camera_list_footer = LayoutInflater.from(this).inflate(R.layout.layout_camera_list_footer, null)
        cameraListAdapter.addFooterView(layout_camera_list_footer)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        ServerApi.getClassrooms().compose(this.bindUntilEvent(ActivityEvent.DESTROY)).doOnTerminate { srl_refresh.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<ClassroomEntity>() {

            override fun onNext(classroomEntity: ClassroomEntity) {
                EZOpenSDK.getInstance().setAccessToken(classroomEntity.data.addition.data)
                if (::layout_camera_list_footer.isInitialized) {
                    layout_camera_list_footer.findViewById<TextView>(R.id.tv_expire_time).text = "到期时间:  ${classroomEntity.data.addition.addition}"

                }
                cameraListAdapter.setNewData(classroomEntity.data.data)
//                doAsync {
                try {
                    val deviceInfo = classroomEntity.data.data[0].kgCamera
//                        val deviceInfo1 = EZOpenSDK.getInstance().getDeviceInfo(deviceInfo.deviceSerial)
//                        LogUtils.d(deviceInfo1)
                    val classroomImage = classroomEntity.data.data[0].classroomImage
//                    preparePlay(deviceInfo, classroomImage)
                } catch (e: BaseException) {
                    toast(e.localizedMessage + e.errorCode)
                }
                sfl_lyt_state.normal()
//                }

            }

            override fun onError(e: Throwable) {
                super.onError(e)
                sfl_lyt_state.error()
            }
        })
    }

    class CameraListAdapter : BaseQuickAdapter<ClassroomEntity.WrapperData.Data, BaseViewHolder>(R.layout.layout_camera_list_item) {

        override fun convert(helper: BaseViewHolder, item: ClassroomEntity.WrapperData.Data) {
            helper.setText(R.id.tv_show_name, item.showName)
            val myOptions = RequestOptions()
                    .centerCrop()
                    .transform(GlideRoundTransform(helper.itemView.context, 5))
            if (item.unWatch == 1) {
                Glide.with(helper.itemView.context).load(item.classroomImage).apply(myOptions).into(helper.getView(R.id.iv_classroom_image))
            } else {
                helper.setImageResource(R.id.iv_classroom_image, R.drawable.unwatch)
            }
        }

    }
}
