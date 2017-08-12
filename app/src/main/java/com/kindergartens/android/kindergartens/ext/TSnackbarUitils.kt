package com.kindergartens.android.kindergartens.ext

import android.app.Activity
import com.kindergartens.android.kindergartens.R
import com.trycatch.mysnackbar.Prompt
import com.trycatch.mysnackbar.TSnackbar
import org.jetbrains.anko.contentView

/**
 * Created by zhangruiyu on 2017/8/8.
 */
class TSnackbarUitils {
    companion object {
        fun toSuccess(activity: Activity, message: String): TSnackbar {
            return TSnackbar.make(activity.contentView!!
                    , message, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.SUCCESS)
                    .setBackgroundColor(activity.getColorSource(R.color.primary_dark))
        }

        fun toFail(activity: Activity, message: String): TSnackbar {
            return TSnackbar.make(activity.contentView!!, message, TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN).setPromptThemBackground(Prompt.ERROR)
//                    .setBackgroundColor(activity.getColorSource(R.color.accent))
        }
    }
}
