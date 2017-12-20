package android.kindergartens.com.core.modular.eat

import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.eat.data.EatEntity
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mazouri.tools.Tools
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.activity_eat.*
import me.iwf.photopicker.PhotoPreview
import org.jetbrains.anko.*
import java.util.*
import kotlin.collections.HashMap

class EatActivity : BaseToolbarActivity() {
    var eatData: HashMap<String, EatEntity.Data> = HashMap()
    //当前选择的天
    var currentDay: String = Tools.time().getNowTimeString("yyyy-MM-dd")
    var currentMonth: String = Tools.time().getNowTimeString("yyyy-MM-dd")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat)
        val waveSwipeHeader = MaterialHeader(this)
        waveSwipeHeader.setColorSchemeColors(R.color.primary_dark, R.color.bangumi_index_green_bg)
        srf_eat_refresh.refreshHeader = waveSwipeHeader
        srf_eat_refresh.setEnableHeaderTranslationContent(false)
        refreshData(currentDay)
        /*  srf_eat_refresh.setOnRefreshListener {
              refreshData(Tools.time().nowTimeString)
          }*/
        /* spinner.adapter = MyAdapter(
                 toolbar.context,
                 arrayOf("本周", "上周", "上上周"))*/
        /*  val eatFragment = EatFragment.newInstance()
          supportFragmentManager.beginTransaction()
                  .replace(R.id.container, eatFragment)
                  .commit()
          spinner.onItemSelectedListener = object : OnItemSelectedListener {
              override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                  eatFragment.refreshData(position)
              }

              override fun onNothingSelected(parent: AdapterView<*>) {}
          }
  */
        UserdataHelper.haveOnlineLet {
            if (it.roleCode != null && it.roleCode!!.toInt() < 1) {
                fab.visibility = GONE
            } else {
                fab.visibility = VISIBLE
                fab.setOnClickListener { _ ->
                    startActivityForResult<EditEatActivity>(100, "currentDay" to currentDay)
                }
            }
        }


        calendar_view.shouldAnimateOnEnter(true)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setOnDateClickListener({
                    currentDay = Tools.time().date2String(it, "yyyy-MM-dd")
                    refreshView(currentDay)
                })
                .setOnMonthChangeListener({
                    currentMonth = Tools.time().date2String(it, "yyyy-MM-dd")
                    currentDay = currentMonth
                    refreshData(currentMonth)
                })



        calendar_view.update(Calendar.getInstance(Locale.getDefault()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        refreshData(currentDay)
    }

    private fun refreshData(date: String) {
        srf_eat_refresh.autoRefresh(0)
        ServerApi.getEatInfoList(date).doOnTerminate { srf_eat_refresh.finishRefresh(100) }.subscribe(object : CustomNetErrorWrapper<EatEntity>() {
            override fun onNext(t: EatEntity) {
//                eatData.clear()
                t.data.forEach {
                    eatData.put(it.createTime, it)
                }
                calendar_view.markDateAsSelected(Tools.time().string2Date(currentDay, "yyyy-MM-dd"))
                refreshView(currentDay)
            }

        })
    }

    private fun refreshView(date: String) {
//        calendar_view.get
        llc_rcv_eat.removeAllViews()
        ll_eat_pic.removeAllViews()
        val currentData = eatData[date]
        if (currentData == null) {
            toast("当天暂无数据")
        } else {
            hsv_eat.visibility = if (currentData.eatUrls.isNotEmpty()) View.VISIBLE else GONE
            setUpText("早餐", currentData.breakfast)
            setUpText("午餐", currentData.lunch)
            setUpText("下午加餐", currentData.supper)
            currentData.eatUrls.forEachIndexed { index, s ->
                val imageView = ImageView(ctx)
                val layoutParams = LinearLayout.LayoutParams(dimen(R.dimen.edit_eat_image_width), ViewGroup.LayoutParams.MATCH_PARENT)
                layoutParams.margin = dip(10)
                imageView.layoutParams = layoutParams
                Glide.with(ctx)
                        .load(s).apply(RequestOptions.centerCropTransform())
                        .into(imageView)
                imageView.setOnClickListener({
                    PhotoPreview.builder()
                            .setPhotos(currentData.eatUrls)
                            .setCurrentItem(index)
                            .setShowDeleteButton(false)
                            .start(act)
                })
                ll_eat_pic.addView(imageView)
            }
        }
    }

    private val colors = arrayOf(R.color.yellow, R.color.blue, R.color.bangumi_index_green_bg, R.color.purple)
    private fun setUpText(title: String, content: String) {
        val inflate = LayoutInflater.from(this).inflate(R.layout.layout_eat_content, null)
        val tv_title = inflate.find<TextView>(R.id.tv_title)
        val tv_content = inflate.find<TextView>(R.id.tv_content)
        tv_title.text = title
        tv_content.text = content
        tv_content.textColor = colors[llc_rcv_eat.childCount]
        llc_rcv_eat.addView(inflate)
    }


}
