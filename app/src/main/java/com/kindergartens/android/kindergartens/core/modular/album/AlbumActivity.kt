package com.kindergartens.android.kindergartens.core.modular.album

import android.os.Bundle
import android.support.design.widget.Snackbar
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import kotlinx.android.synthetic.main.activity_album.*

class AlbumActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
