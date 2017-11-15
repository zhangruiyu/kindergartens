package com.kindergartens.android.kindergartens.core.modular.album

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kindergartens.android.kindergartens.R

/**
 * A placeholder fragment containing a simple view.
 */
class AlbumActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }
}
