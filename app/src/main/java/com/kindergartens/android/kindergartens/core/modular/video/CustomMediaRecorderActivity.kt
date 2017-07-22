package com.kindergartens.android.kindergartens.core.modular.video

import android.app.Activity
import android.view.Menu
import com.kindergartens.android.kindergartens.R
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig
import org.jetbrains.anko.startActivity

/**
 * Created by zhangruiyu on 2017/7/22.
 */

class CustomMediaRecorderActivity : MediaRecorderActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_video_recorder, menu)
        return true
    }


    companion object {

        /**
         * @param context
         * *
         * @param overGOActivityName 录制结束后需要跳转的Activity全类名
         */
        fun goSmallVideoRecorder(context: Activity, overGOActivityName: String, mediaRecorderConfig: MediaRecorderConfig) {
            context.startActivity<CustomMediaRecorderActivity>(OVER_ACTIVITY_NAME to overGOActivityName, MEDIA_RECORDER_CONFIG_KEY to mediaRecorderConfig)
        }

    }
}