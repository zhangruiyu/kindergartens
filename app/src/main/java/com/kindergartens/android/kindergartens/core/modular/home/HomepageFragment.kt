package com.kindergartens.android.kindergartens.core.modular.home

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.modular.classroom.ClassRoomActivity
import com.kindergartens.android.kindergartens.core.modular.home.data.HomepageItemBean
import com.kindergartens.android.kindergartens.core.modular.schoolmessage.SchoolMessageActivity
import com.kindergartens.android.kindergartens.core.ui.NGGuidePageTransformer
import com.kindergartens.android.kindergartens.ext.width
import com.youth.banner.Banner
import com.youth.banner.loader.ImageLoader
import kotlinx.android.synthetic.main.fragment_homepage.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.dimen
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast


/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class HomepageFragment : BaseFragment() {
    //    lateinit private var bannerOptions: RequestOptions
    lateinit private var banner: Banner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        bannerOptions = RequestOptions()/*.placeholder(R.drawable.banner_normal).error(R.drawable.banner_normal)*/
//                .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).override(width, dimen(R.dimen.general_banner_height))
        banner = Banner(ctx)
                .setImageLoader(object : ImageLoader() {
                    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
                        Glide.with(context)
                                .load(path).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).override(width, dimen(R.dimen.general_banner_height))
//                                .transition(DrawableTransitionOptions().crossFade(500))
//                                .thumbnail(Glide.with(context).load(R.drawable.banner_normal).apply(bannerOptions))
                                .into(imageView)
                    }

                }).setPageTransformer(true, NGGuidePageTransformer()).setDelayTime(4000).start()
        banner.setBackgroundResource(R.drawable.banner_normal)
        banner.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.general_banner_height).toInt())
    }

    //摄像头放中间 方便老师看见
    val itemBeans = mutableListOf(HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "校园消息"),
            HomepageItemBean(R.drawable.home_fab_icon, "校园消息"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "在线视频"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"),
            HomepageItemBean(R.drawable.home_fab_icon, "宝贝饮食"))


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //banner大小是因为glide没设置的问题
        return inflater?.inflate(R.layout.fragment_homepage, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        banner.setImages(mutableListOf("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/0C/0E/ChMkJ1j1gQ6ILskaADPWcgfgSeYAAbvmQAADeEAM9aK508.jpg", "http://avatar.csdn.net/4/E/F/1_lyhhj.jpg", "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2885786814,3634989667&fm=80&w=179&h=119&img.JPEG"))
                .start()
        val homepageAdapter = HomepageAdapter()
        homepageAdapter.setNewData(itemBeans)
        srf_homepage_refresh.setScrollUpChild(rl_homepage_content)
        rl_homepage_content.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        rl_homepage_content.adapter = homepageAdapter
        rl_homepage_content.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                toast(position.toString())
                when (position) {
                    1 -> startActivity<SchoolMessageActivity>()
                    4 -> {
                        startActivity<ClassRoomActivity>()
                    }
                }
//                val allSchoolInfo = (baseQuickAdapter as HomepageAdapter).data[i]
//                val intent = Intent(context, SchoolDetailActivity::class.java)
//                intent.putExtra(Consts.IntentKeyConsts.HomepageSchoolIDKey, allSchoolInfo.t.schoolId)
//                TransitionsHeleper.startActivity(activity, intent, view.findViewById(R.id.video_preview),
//                        baseQuickAdapter.data[i].t.shcool_Picture)
            }
        })
        homepageAdapter.addHeaderView(banner)

    }

    override fun onInVisible() {
        super.onInVisible()
        srf_homepage_refresh.isRefreshing = false
        banner.stopAutoPlay()
    }

    override fun onVisible() {
        super.onVisible()
        banner.startAutoPlay()
    }
}
