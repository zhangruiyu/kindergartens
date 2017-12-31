package android.kindergartens.com.core.tools

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.kindergartens.com.R
import android.kindergartens.com.ext.safeDismiss
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.jetbrains.anko.find


/**
 * Created by zhangruiyu on 2017/12/31.
 */

class DialogManager {
    companion object {
        fun getJoinSchoolDialog(context: Context, schoolName: String, address: String, tel: String, schoolPicture: String): Dialog {
            val dialog = Dialog(context,
                    R.style.Dialog_Translucence)
            val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_join_school, null)
            /*  inflate.setOnClickListener({
                  dialog.safeDismiss()
              })*/
            Glide.with(context).load(schoolPicture)/*.apply(bitmapTransform(CircleCrop()))*/
                    .into(inflate.find(R.id.iv_school_pic))
            inflate.find<TextView>(R.id.tv_school_name).text = schoolName
            inflate.find<TextView>(R.id.tv_school_address).text = address
            inflate.find<TextView>(R.id.tv_school_tel).text = tel
            inflate.find<TextView>(R.id.tv_school_tel).setOnClickListener({
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            })
            inflate.find<ImageView>(R.id.aciv_close).setOnClickListener({
                dialog.safeDismiss()
            })
            dialog.setContentView(inflate)
            return dialog
        }

    }
}
