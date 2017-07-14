package com.kindergartens.android.kindergartens.core.modular.dynamic

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import org.jetbrains.anko.toast

class EditDynamicActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dynamic)
        init()
    }

    private fun init() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dynamic, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        toast("item+${item.itemId}")
        return super.onOptionsItemSelected(item)
    }
}
