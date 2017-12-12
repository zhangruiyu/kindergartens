package android.kindergartens.com.core.modular.eat

import android.content.Context
import android.content.res.Resources.Theme
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseToolbarActivity
import android.kindergartens.com.core.modular.eat.data.EatEntity
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat)
        val waveSwipeHeader = MaterialHeader(this)
        waveSwipeHeader.setColorSchemeColors(R.color.primary_dark,R.color.bangumi_index_green_bg)
        srf_eat_refresh.refreshHeader = waveSwipeHeader
        srf_eat_refresh.setEnableHeaderTranslationContent(false)
        refreshData(Tools.time().nowTimeString)
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
        fab.setOnClickListener { view ->
            Snackbar.make(view, "点赞,或者建议", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        calendar_view.shouldAnimateOnEnter(true)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setOnDateClickListener({
                    toast(Tools.time().date2String(it).toString() + "aa")
                    refreshView(Tools.time().date2String(it, "yyyy-MM-dd"))
                })
                .setOnMonthChangeListener({
                    refreshData(Tools.time().date2String(it).toString())
                })
                .setOnDateLongClickListener({
                    toast(it.toString() + "cc")
                })
                .setOnMonthTitleClickListener({
                    toast(it.toString() + "dd")
                })


        calendar_view.update(Calendar.getInstance(Locale.getDefault()))
    }

    private fun refreshData(date: String) {
        srf_eat_refresh.autoRefresh(0)
        ServerApi.getEatInfoList(date).doOnTerminate { srf_eat_refresh.finishRefresh(100) }.subscribe(object : CustomNetErrorWrapper<EatEntity>() {
            override fun onNext(t: EatEntity) {
                eatData.clear()
                t.data.forEach {
                    eatData.put(it.createTime, it)
                }

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
            hsv_eat.visibility = if (currentData.eatUrls.size > 0) View.VISIBLE else GONE
            setUpText("早餐", currentData.breakfast)
            setUpText("午饭", currentData.lunch)
            setUpText("下午加餐", currentData.supper)
            currentData.eatUrls.forEachIndexed { index, s ->
                val imageView = ImageView(ctx)
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
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


    private class MyAdapter(context: Context, objects: Array<String>) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, objects), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper = ThemedSpinnerAdapter.Helper(context)

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            view = if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            } else {
                convertView
            }

            val textView = view.findViewById<View>(android.R.id.text1) as TextView
            textView.text = getItem(position)

            return view
        }

        override fun setDropDownViewTheme(theme: Theme?) {
            mDropDownHelper.dropDownViewTheme = theme
        }

        override fun getDropDownViewTheme(): Theme? {
            return mDropDownHelper.dropDownViewTheme
        }
    }

}
