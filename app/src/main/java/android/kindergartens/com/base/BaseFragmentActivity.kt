package android.kindergartens.com.base

import org.jetbrains.anko.AnkoLogger

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseFragmentActivity : BaseToolbarFragmentActivity(), AnkoLogger {
    override fun setUpToolbar() {
        //不去搞toolbar
    }
}