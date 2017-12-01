package android.kindergartens.com.core.modular.album.data

import com.chad.library.adapter.base.entity.SectionEntity

/**
 * Created by zhangruiyu on 2017/11/16.
 */
class AlbumSection : SectionEntity<ArrayList<AlbumEntity.Data.Addition>> {
    constructor(isHeader: Boolean, header: String?) : super(isHeader, header)

    constructor(t: ArrayList<AlbumEntity.Data.Addition>) : super(t)
}