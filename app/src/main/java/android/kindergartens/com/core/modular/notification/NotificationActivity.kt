package android.kindergartens.com.core.modular.notification

import android.app.NotificationManager
import android.content.Context
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseActivity
import android.kindergartens.com.core.MainActivity
import android.kindergartens.com.core.database.UserdataHelper
import android.kindergartens.com.receiver.AlarmReceiver.Companion.AlarmReceiverNotificationId
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import kotlinx.android.synthetic.main.activity_notification.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity

class NotificationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        UserdataHelper.haveOnlineLet {
            Glide.with(ctx).load(it.avatar).apply(bitmapTransform(CircleCrop()))
                    .into(iv_avatar)
        }

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(AlarmReceiverNotificationId)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        startActivity<MainActivity>()
        finish()
    }
}
