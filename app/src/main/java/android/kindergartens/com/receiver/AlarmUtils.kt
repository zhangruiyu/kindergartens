package android.kindergartens.com.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import java.util.*

/**
 * Created by zhangruiyu on 2017/12/4.
 */

object AlarmUtils {
    fun setAlarmTime(context: Context, timeInMillis: Long, time: Int, sender: PendingIntent) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //参数2是开始时间、参数3是允许系统延迟的时间
            am.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, time.toLong(), sender)
        } else {
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeInMillis, time.toLong(), sender)
        }
    }

    @JvmStatic
    fun setAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0);

        var firstTime = SystemClock.elapsedRealtime() // 开机之后到现在的运行时间(包括睡眠时间)
        val systemTime = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
// 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.timeZone = TimeZone.getTimeZone("GMT+8")
        calendar.set(Calendar.HOUR_OF_DAY, 13)
        calendar.set(Calendar.MINUTE, 10)
        //防止同分钟重复
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 0)
// 选择的定时时间
        var selectTime = calendar.timeInMillis
// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            Toast.makeText(context, "设置的时间小于当前时间", Toast.LENGTH_SHORT).show()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectTime = calendar.timeInMillis
        }
// 计算现在时间到设定时间的时间差
        val time = selectTime - systemTime
        firstTime += time
// 进行闹铃注册
        AlarmUtils.setAlarmTime(context, firstTime, 60 * 60 * 24 * 1000, sender)
        Toast.makeText(context, "设置重复闹铃成功! ", Toast.LENGTH_LONG).show()

    }
}
