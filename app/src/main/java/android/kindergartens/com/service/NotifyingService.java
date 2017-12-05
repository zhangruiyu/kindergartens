package android.kindergartens.com.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.kindergartens.com.R;
import android.kindergartens.com.core.modular.setting.SettingActivity;
import android.kindergartens.com.core.tools.CustomTimeUtil;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import com.mazouri.tools.Tools;
import com.mazouri.tools.constants.ConstantsTool;

/**
 * Created by zhangruiyu on 2017/12/4.
 */

public class NotifyingService extends Service {
    // 状态栏通知的管理类对象，负责发通知、清楚通知等
    private NotificationManager mNM;
    // 使用Layout文件的对应ID来作为通知的唯一识别
    private static int MOOD_NOTIFICATIONS = R.layout.activity_main;

    /**
     * Android给我们提供ConditionVariable类，用于线程同步。提供了三个方法block()、open()、close()。 void
     * block() 阻塞当前线程，直到条件为open 。 void block(long timeout)阻塞当前线程，直到条件为open或超时
     * void open()释放所有阻塞的线程 void close() 将条件重置为close。
     */
    private ConditionVariable mCondition;

    @Override
    public void onCreate() {
        // 状态栏通知的管理类对象，负责发通知、清楚通知等
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 启动一个新个线程执行任务，因Service也是运行在主线程，不能用来执行耗时操作
        Thread notifyingThread = new Thread(null, mTask, "NotifyingService");
        mCondition = new ConditionVariable(false);
        notifyingThread.start();
        showNotification();
    }

    @Override
    public void onDestroy() {
        // 取消通知功能
        mNM.cancel(MOOD_NOTIFICATIONS);
        // 停止线程进一步生成通知
        mCondition.open();
    }

    /**
     * 生成通知的线程任务
     */
    private Runnable mTask = new Runnable() {
        public void run() {
            Tools.time().getTimeSpanByNow(CustomTimeUtil.getDate(17, 30, 0, 0), ConstantsTool.TimeUnit.MIN);
            for (int i = 0; i < 4; ++i) {
                // 生成带stat_happy及status_bar_notifications_happy_message内容的通知
                showNotification();
                if (mCondition.block(5 * 1000))
                    break;
                // 生成带stat_neutral及status_bar_notifications_ok_message内容的通知
                showNotification();
                if (mCondition.block(5 * 1000))
                    break;
                // 生成带stat_sad及status_bar_notifications_sad_message内容的通知
                showNotification();
                if (mCondition.block(5 * 1000))
                    break;
            }
            // 完成通知功能，停止服务。
//            NotifyingService.this.stopSelf();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void showNotification() {
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, SettingActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("我要放学啦")
                .setContentText("来接我吧,爸爸")
                .setContentIntent(contentIntent);

        /**
         * 注意,我们使用出来。incoming_message ID 通知。它可以是任何整数,但我们使用 资源id字符串相关
         * 通知。它将永远是一个独特的号码在你的 应用程序。
         */
        mNM.notify(MOOD_NOTIFICATIONS, builder.build());
    }

    // 这是接收来自客户端的交互的对象. See
    private final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,
                                     int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

}