package android.kindergartens.com.ext

import android.support.design.widget.FloatingActionButton
import android.view.View


/**
 * Created by zhangruiyu on 2017/7/13.
 */
fun FloatingActionButton.hideButton() {
    // Cancel any animation from the default behavior
//    animate().cancel()
//    animate()
//            .scaleX(0f)
//            .scaleY(0f)
////            .setInterpolator(AccelerateInterpolator())
//            .start();
    visibility = View.INVISIBLE
}

fun FloatingActionButton.showButton() {
    visibility = View.VISIBLE
    /* alpha = 0f
     scaleX = 0f
     scaleY = 0f
     animate()
             .alpha(1f)
             .scaleX(1f)
             .scaleY(1f)
             .setDuration(200).setInterpolator(FastOutLinearInInterpolator()).start()*/
//    animate()
//            .scaleX(1f)
//            .scaleY(1f)
////            .setInterpolator(OvershootInterpolator())
//            .start();

}