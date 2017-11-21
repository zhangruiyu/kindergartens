package com.kindergartens.android.kindergartens.service

import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.allenliu.versionchecklib.core.AVersionService
import com.google.gson.Gson
import com.kindergartens.android.kindergartens.base.BaseActivity
import com.kindergartens.android.kindergartens.core.modular.checkupdate.CustomVersionDialogActivity

class CheckUpdateService : AVersionService() {
    override fun onResponses(service: AVersionService?, response: String) {
        Log.e("CheckUploadService", response)
        BaseActivity.runActivity?.run {
            val gson = Gson()
            val fromJson = gson.fromJson<CheckUpdateEntity>(response, CheckUpdateEntity::class.java)
            if (fromJson.data.checkState != 0) {
                CustomVersionDialogActivity.isForceUpdate = fromJson.data.checkState == 2
                showVersionDialog(fromJson.data.downloadUrl, "检测到新版本是否更新?", fromJson.data.message)
            }

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }
}

