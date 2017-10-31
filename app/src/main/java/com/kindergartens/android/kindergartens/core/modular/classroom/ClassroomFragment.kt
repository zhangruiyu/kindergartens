package com.kindergartens.android.kindergartens.core.modular.classroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kindergartens.android.kindergartens.R
import com.kindergartens.android.kindergartens.base.BaseFragment
import com.kindergartens.android.kindergartens.core.modular.classroom.data.ClassroomEntity
import kotlinx.android.synthetic.main.fragment_class_room.*

/**
 * Created by zhangruiyu on 2017/8/20.
 */
class ClassroomFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_class_room, container, false)
//        val textView = rootView.findViewById<TextView>(R.id.section_label) as TextView
//        textView.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val data = arguments.getSerializable(ARG_SECTION_DATA) as ClassroomEntity.Data
        if (data.isCorridor == 0) {
            tv_classroom_student_count.text = data.childCount.toString()
        }
        tv_classroom_synopsis.text = data.synopsis
        tv_classroom_student_count.visibility = if (data.isCorridor == 0) View.VISIBLE else View.GONE


    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"
        private val ARG_SECTION_DATA = "section_data"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int, data: ClassroomEntity.Data): ClassroomFragment {
            val fragment = ClassroomFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            args.putSerializable(ARG_SECTION_DATA, data)
            fragment.arguments = args
            return fragment
        }
    }
}