package android.kindergartens.com.core.modular.album

import android.graphics.Rect
import android.kindergartens.com.R
import android.kindergartens.com.base.BaseFragment
import android.kindergartens.com.core.modular.album.data.AlbumEntity
import android.kindergartens.com.core.modular.album.data.AlbumSection
import android.kindergartens.com.ext.getWidth
import android.kindergartens.com.net.CustomNetErrorWrapper
import android.kindergartens.com.net.ServerApi
import android.os.Bundle
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_album.*
import me.iwf.photopicker.PhotoPreview
import org.jetbrains.anko.dip
import org.jetbrains.anko.support.v4.ctx

/**
 * A placeholder fragment containing a simple view.
 */
class AlbumActivityFragment : BaseFragment(), OnRefreshListener {
    lateinit var albumAdapter: AlbumAdapter
    override fun onRefresh(p0: RefreshLayout?) {
        ServerApi.getSchoolAlbum().doOnTerminate { srl_refresh.finishRefresh() }.subscribe(object : CustomNetErrorWrapper<AlbumEntity>() {
            override fun onNext(t: AlbumEntity) {
                val albumList = ArrayList<AlbumSection>()
                t.data.forEach {
                    albumList.add(AlbumSection(true, it.data))
                    albumList.add(AlbumSection(it.addition))
                }
                albumAdapter.setNewData(albumList)
            }


        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        albumAdapter = AlbumAdapter(this)
        rv_album.layoutManager = LinearLayoutManager(ctx)
        rv_album.adapter = albumAdapter
        srl_refresh.setOnRefreshListener(this)
        srl_refresh.autoRefresh()

    }
}

class AlbumAdapter(val albumActivityFragment: AlbumActivityFragment) : BaseSectionQuickAdapter<AlbumSection, BaseViewHolder>(R.layout.fragment_album_adapter_item, R.layout.fragment_album_adapter_item_head, null) {
    val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView?, state: RecyclerView.State?) {
            val dip = view.context.dip(3)
            outRect.top = dip * 5
            outRect.left = dip / 5
            outRect.right = dip / 5

        }
    }

    override fun convertHead(helper: BaseViewHolder, item: AlbumSection) {
        helper.setText(R.id.tv_title, item.header)
    }

    override fun convert(helper: BaseViewHolder, item: AlbumSection) {
        val rcv_album_pic = helper.getView<RecyclerView>(R.id.rcv_album_pic)
        rcv_album_pic.layoutManager = GridLayoutManager(rcv_album_pic.context, 4)
        rcv_album_pic.addItemDecoration(itemDecoration)
        val albumPicAdapter = AlbumPicAdapter(item.t)
        albumPicAdapter.setOnItemChildClickListener { adapter, view, position ->

            val preImages = ArrayList<String>()
            preImages.addAll(item.t.map { it.picUrl })
            PhotoPreview.builder()
                    .setPhotos(ArrayList<String>(item.t.map { it.picUrl }))
                    .setCurrentItem(position)
                    .setShowDeleteButton(false)
                    .start(albumActivityFragment.activity)

        };
        rcv_album_pic.adapter = albumPicAdapter
    }

}

class AlbumPicAdapter(data: ArrayList<AlbumEntity.Data.Addition>) : BaseQuickAdapter<AlbumEntity.Data.Addition, BaseViewHolder>
(R.layout.fragment_album_adapter_pic_item, data) {
    override fun convert(helper: BaseViewHolder, item: AlbumEntity.Data.Addition) {
        val imageView = helper.getView<AppCompatImageView>(R.id.aciv_album_pic)
        val width = imageView.context.getWidth() / 4 - imageView.context.dip(20)
        imageView.layoutParams = ViewGroup.LayoutParams(width, width)
        helper.addOnClickListener(R.id.aciv_album_pic)
        Glide.with(imageView.context).load(item.picUrl).apply(RequestOptions.centerCropTransform())
                .into(imageView)

    }


}