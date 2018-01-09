package android.kindergartens.com.base

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.umeng.analytics.MobclickAgent
import com.umeng.message.PushAgent
import java.util.*

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open  class BaseActivity : RxAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PushAgent.getInstance(this).onAppStart()

    }
    override fun onResume() {
        super.onResume()
        runActivity = this
        activists.add(this)
        MobclickAgent.onResume(this);
    }

    override fun onStop() {
        super.onStop()
//        runActivity = null
        activists.remove(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this);
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        var runActivity: Activity? = null
        @JvmStatic
        var activists: LinkedList<Activity> = LinkedList()

        @JvmStatic
        fun exitApp() {
            for (activist in activists) {
                activist.finish()
            }
            activists.clear()
        }
    }
}