package android.kindergartens.com.core.modular.home

import android.content.Context
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.album.AlbumActivity
import android.kindergartens.com.core.modular.browser.BrowserActivity
import android.kindergartens.com.core.modular.classroom.ClassroomActivity
import android.kindergartens.com.core.modular.classroommessage.ClassroomMessageActivity
import android.kindergartens.com.core.modular.eat.EatActivity
import android.kindergartens.com.core.modular.home.data.BannerEntity
import android.kindergartens.com.core.modular.home.data.HomepageItemBean
import android.kindergartens.com.core.modular.schoolmessage.SchoolMessageActivity
import android.kindergartens.com.ext.TSnackbarUtils
import android.kindergartens.com.ext.width
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.kindergartens.android.kindergartens.core.ui.NGGuidePageTransformer
import com.youth.banner.Banner
import com.youth.banner.loader.ImageLoader
import com.youth.banner.view.BannerViewPager
import kotlinx.android.synthetic.main.fragment_homepage.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.*


/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class HomepageFragment : BaseFragment() {
    lateinit private var bannerOptions: RequestOptions
    lateinit private var banner: Banner
    var bannerData = ArrayList<BannerEntity.WrapperInfo.Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bannerOptions = RequestOptions().placeholder(R.drawable.banner_normal).error(R.drawable.banner_normal)
                .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().override(width, dimen(R.dimen.general_banner_height))
        banner = Banner(ctx)
                .setImageLoader(object : ImageLoader() {
                    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
                        Glide.with(context)
                                .load(path)
//                                .transition(DrawableTransitionOptions().crossFade(500))
//                                .thumbnail(Glide.with(context).load(R.drawable.banner_normal))
                                .apply(bannerOptions)
                                .into(imageView)
                    }

                }).setPageTransformer(true, NGGuidePageTransformer()).setDelayTime(4000).start()
//        banner.setBackgroundResource(R.drawable.banner_normal)
        banner.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.general_banner_height).toInt())
        val viewPager = banner.find<BannerViewPager>(R.id.bannerViewPager)
        banner.setOnBannerListener {
            LogUtils.d("viewPager.currentItem===" + viewPager.currentItem)
            val data = bannerData[viewPager.currentItem - 1]
            BrowserActivity.launch(act, data.url, data.title)
        }
    }

    //摄像头放中间 方便看见
    val itemBeans = mutableListOf(
            HomepageItemBean(R.drawable.homepage_all_message, "通知"),
            HomepageItemBean(R.drawable.homepage_school_message, "校园消息"),
            HomepageItemBean(R.drawable.homepage_eat, "饮食日历"),
            HomepageItemBean(R.drawable.homepage_album, "班级相册"),
            HomepageItemBean(R.drawable.homepage_video, "在线视频"),
            HomepageItemBean(R.drawable.homepage_video, "在线抓娃娃")
    )


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //banner大小是因为glide没设置的问题
        return inflater?.inflate(R.layout.fragment_homepage, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val homepageAdapter = HomepageAdapter(ctx)
        homepageAdapter.setNewData(itemBeans)
//        srf_homepage_refresh.setScrollUpChild(rl_homepage_content)
        rl_homepage_content.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        rl_homepage_content.adapter = homepageAdapter
        rl_homepage_content.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
//                toast(position.toString())
                when (position) {
                    0 -> startActivity<SchoolMessageActivity>()
                    1 -> UserdataHelper.haveNoOnlineLet { startActivity<ClassroomMessageActivity>() }
                    2 -> UserdataHelper.haveNoOnlineLet { startActivity<EatActivity>() }
                    3 -> UserdataHelper.haveNoOnlineLet { startActivity<AlbumActivity>() }
                    4 -> UserdataHelper.haveNoOnlineLet { startActivity<ClassroomActivity>() }
                    5 -> toast("暂不可用")
                }
//                val allSchoolInfo = (baseQuickAdapter as HomepageAdapter).data[i]
//                val intent = Intent(context, SchoolDetailActivity::class.java)
//                intent.putExtra(Consts.IntentKeyConsts.HomepageSchoolIDKey, allSchoolInfo.t.schoolId)
//                TransitionsHeleper.startActivity(activity, intent, view.findViewById(R.id.video_preview),
//                        baseQuickAdapter.data[i].t.shcool_Picture)
            }
        })
        homepageAdapter.addHeaderView(banner)
        srf_homepage_refresh.autoRefresh()
        srf_homepage_refresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        ServerApi.getBanner().doOnTerminate { srf_homepage_refresh.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<BannerEntity>() {
            override fun onNext(t: BannerEntity) {
                this@HomepageFragment.bannerData = t.data.data
                banner.setImages(this@HomepageFragment.bannerData.map { it.picUrl })
                        .start()
                val addition = t.data.addition
                if (addition > 0) {
                    TSnackbarUtils.toSuccess(act, "每日登陆积分 +$addition").show()
                }
            }

        })
    }

    override fun onInVisible() {
        super.onInVisible()
        srf_homepage_refresh.finishRefresh()
        banner.stopAutoPlay()
    }

    override fun onVisible() {
        super.onVisible()
        banner.startAutoPlay()
    }
}
