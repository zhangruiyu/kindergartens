package com.kindergartens.android.kindergartens.core

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragmentActivity
import com.kindergartens.android.kindergartens.core.modular.dynamic.EditDynamicActivity
import com.kindergartens.android.kindergartens.core.modular.home.HomepageFragment
import com.kindergartens.android.kindergartens.core.modular.home.OtherFragment
import com.kindergartens.android.kindergartens.core.modular.home.dummy.DynamicFragment
import com.kindergartens.android.kindergartens.core.modular.video.CustomMediaRecorderActivity
import com.kindergartens.android.kindergartens.ext.hideButton
import com.kindergartens.android.kindergartens.ext.showButton
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.startActivity


class MainActivity : BaseFragmentActivity() {
    private val ids = intArrayOf(R.color.accent, R.color.blue, R.color.grey, R.color.orange)
    private var currentTabIndex: Int = 0
    private var index: Int = 0
    private var mTextMessage: TextView? = null
    var homepageFragment = HomepageFragment()
    val dynamicFragment = DynamicFragment()
    val homepageFragment3 = OtherFragment.newInstance()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
        /* R.id.navigation_home -> {
             mTextMessage!!.setText(R.string.title_home)
             return@OnNavigationItemSelectedListener true
         }
         R.id.navigation_dashboard -> {
             mTextMessage!!.setText(R.string.title_dashboard)
             return@OnNavigationItemSelectedListener true
         }
         R.id.navigation_notifications -> {
             mTextMessage!!.setText(R.string.title_notifications)
             return@OnNavigationItemSelectedListener true
         }*/
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigationBar()
        initFragments()
//        val queryList = SQLite.select().from(TUserWrapper::class.java).where(TUserWrapper_Table.token.eq("kgg")).queryList()
//        val navigation = findViewById(R.id.navigation) as BottomNavigationView
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//        print(queryList)
    }

    private fun initFragments() {
        add(fragment = homepageFragment)
        add(fragment = dynamicFragment)
        add(fragment = homepageFragment3)


        supportFragmentManager.beginTransaction().add(R.id.homepage_container, homepageFragment)
                .show(homepageFragment).commit()
    }

    private fun initBottomNavigationBar() {
        bottom_navigation_bar.setMode(BottomNavigationBar.MODE_SHIFTING)
        bottom_navigation_bar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE
                )
        bottom_navigation_bar.addItem(BottomNavigationItem(R.drawable.ic_home_normal).setActiveColorResource(R.color.accent))
                .addItem(BottomNavigationItem(R.drawable.ic_notifications_normal).setActiveColorResource(R.color.blue))
                .addItem(BottomNavigationItem(R.drawable.ic_menu_normal).setActiveColorResource(R.color.grey))
                //                .setFirstSelectedPosition(1)
                .initialise()
        bottom_navigation_bar.setFab(fab_home)
        fab_home.setOnClickListener {
            startActivity<EditDynamicActivity>()
        }
        fab_home.setOnLongClickListener {
            // 录制
            val config = MediaRecorderConfig.Buidler()

                    .fullScreen(false)
                    .smallVideoWidth(360)
                    .smallVideoHeight(480)
                    .recordTimeMax(6000)
                    .recordTimeMin(1500)
                    .maxFrameRate(20)
                    .videoBitrate(600000)
                    .captureThumbnailsTime(1)
                    .build()
            CustomMediaRecorderActivity.goSmallVideoRecorder(this@MainActivity, EditDynamicActivity::class.java.name, config)
            return@setOnLongClickListener true
        }
        bottom_navigation_bar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {
            }

            override fun onTabUnselected(position: Int) = Unit

            override fun onTabSelected(position: Int) {
                changeFragmentByIndex(position)
                toolbar!!.setBackgroundResource(ids[position])
                fab_home?.apply {
                    if (position == 2) {
                        hideButton()
                    } else {
                        showButton()
                    }
                }

            }

        })
        toolbar!!.backgroundResource = ids[bottom_navigation_bar.currentSelectedPosition]
    }

    private fun changeFragmentByIndex(currentIndex: Int) {
        index = currentIndex
        switchFragment()
    }

    private fun switchFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(mFragments!![currentTabIndex])
        if (!mFragments!![index].isAdded) {
            fragmentTransaction.add(R.id.homepage_container, mFragments!![index])
        }
        fragmentTransaction.show(mFragments!![index]).commit()
        currentTabIndex = index

    }

}

