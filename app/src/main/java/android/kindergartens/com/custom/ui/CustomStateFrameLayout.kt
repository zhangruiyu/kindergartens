package android.kindergartens.com.custom.ui

import am.widget.stateframelayout.StateFrameLayout
import android.content.Context
import android.kindergartens.com.R
import android.util.AttributeSet
import android.view.LayoutInflater

/**
 * Created by zhangruiyu on 2018/1/22.
 */
class CustomStateFrameLayout : StateFrameLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val layoutInflater = LayoutInflater.from(context)
        setStateViews(layoutInflater.inflate(R.layout.layout_loading, null), layoutInflater.inflate(R.layout.layout_error, null), layoutInflater.inflate(R.layout.layout_empty, null))
        state = STATE_LOADING
    }
}