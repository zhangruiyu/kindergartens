package com.kindergartens.android.kindergartens.base

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.kindergartens.android.kindergartens.R
import org.jetbrains.anko.find

/**
 * Created by zhangruiyu on 2017/6/21.
 */
open class BaseToolbarActivity : BaseActivity() {
    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUpToolbar()
    }
    private fun setUpToolbar() {
        toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener({ finish() })
    }
}