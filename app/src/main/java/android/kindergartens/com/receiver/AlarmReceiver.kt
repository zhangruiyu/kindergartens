package android.kindergartens.com.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.core.modular.notification.NotificationActivity
import android.os.Build
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.widget.Toast


/**
 * Created by zhangruiyu on 2017/12/4.
 */

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val AlarmReceiverNotificationId = 1001110
    }

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show()
        if (UserdataHelper.haveOnlineUser()) {
            //因为setWindow只执行一次，所以要重新定义闹钟实现循环。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Handler().postDelayed({
                    AlarmUtils.setAlarm(context)
                    //一分钟后再次设置
                }, 60000)
            }
            val contentIntent = PendingIntent.getActivity(
                    context, 0, Intent(context, NotificationActivity::class.java), 0)

            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("我要放学啦")
                    .setContentText("来接我吧,爸爸")
                    .setContentIntent(contentIntent)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(AlarmReceiverNotificationId, builder.build())
        }
    }

}