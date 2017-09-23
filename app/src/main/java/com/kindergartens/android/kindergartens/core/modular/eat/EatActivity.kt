package com.kindergartens.android.kindergartens.core.modular.eat

import android.content.Context
import android.content.res.Resources.Theme
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.ThemedSpinnerAdapter
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseToolbarActivity
import kotlinx.android.synthetic.main.activity_eat.*

class EatActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eat)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        spinner.adapter = MyAdapter(
                toolbar.context,
                arrayOf("本周", "上周", "上上周"))
        val eatFragment = EatFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, eatFragment)
                .commit()
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                eatFragment.refreshData(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "点赞,或者建议", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }


    private class MyAdapter(context: Context, objects: Array<String>) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, objects), ThemedSpinnerAdapter {
        private val mDropDownHelper: ThemedSpinnerAdapter.Helper

        init {
            mDropDownHelper = ThemedSpinnerAdapter.Helper(context)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view: View

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                val inflater = mDropDownHelper.dropDownViewInflater
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            } else {
                view = convertView
            }

            val textView = view.findViewById<View>(android.R.id.text1) as TextView
            textView.text = getItem(position)

            return view
        }

        override fun setDropDownViewTheme(theme: Theme?) {
            mDropDownHelper.dropDownViewTheme = theme
        }

        override fun getDropDownViewTheme(): Theme? {
            return mDropDownHelper.dropDownViewTheme
        }
    }

}
