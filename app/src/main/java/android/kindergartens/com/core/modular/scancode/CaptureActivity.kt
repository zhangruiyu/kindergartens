package android.kindergartens.com.core.modular.scancode

import android.kindergartens.com.base.BaseActivity.Companion.activists
import android.kindergartens.com.base.BaseActivity.Companion.runActivity
import android.os.Bundle
import com.umeng.analytics.MobclickAgent
import com.uuzuche.lib_zxing.activity.CaptureActivity

class CaptureActivity : CaptureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        const val CAPTURE_REQUEST_CODE = 2018
    }
}
