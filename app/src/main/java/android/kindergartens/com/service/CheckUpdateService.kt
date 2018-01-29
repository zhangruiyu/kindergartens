package android.kindergartens.com.service

import android.content.Intent
import android.kindergartens.com.base.BaseActivity
import android.kindergartens.com.core.modular.checkupdate.CustomVersionDialogActivity
import android.kindergartens.com.core.modular.setting.SettingActivity
import android.os.IBinder
import android.util.Log
import com.allenliu.versionchecklib.core.AVersionService
import com.google.gson.Gson
import org.jetbrains.anko.toast

class CheckUpdateService : AVersionService() {
    override fun onResponses(service: AVersionService?, response: String) {
        Log.e("CheckUploadService", response)
        BaseActivity.runActivity?.run {
            val gson = Gson()
            val fromJson = gson.fromJson<CheckUpdateEntity>(response, CheckUpdateEntity::class.java)
            if (fromJson.data.checkState != 0) {
                CustomVersionDialogActivity.isForceUpdate = fromJson.data.checkState == 2
                showVersionDialog(fromJson.data.downloadUrl, "检测到新版本是否更新?", fromJson.data.message)
            } else {
                if (BaseActivity.runActivity is SettingActivity) {
                    toast("您的版本为最新版本,无需更新!")
                }

            }

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }
}

