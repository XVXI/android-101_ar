package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.transcode.smartcity101p2.adapter.YoutubePlayListAdapter
import com.transcode.smartcity101p2.contract.YoutubePlayListFragmentContract
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.YoutubePlayListResponse
import com.transcode.smartcity101p2.presenter.YoutubePlayListFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_youtubeplaylist.*
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.transcode.smartcity101p2.R


class YoutubePlayListFragment : CoreFragment(), YoutubePlayListFragmentContract.View, YoutubePlayListAdapter.ClickItem {
    override fun onclickItem(res: YoutubePlayListResponse.PlayListItem) {
        res.snippet?.let {
            val video_id = it.resourceId?.videoId ?: ""
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$video_id"))
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$video_id"))
            context?.let {
                try {
                    it.startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    it.startActivity(webIntent)
                }
            }
        }
    }

    override fun updateList(res: YoutubePlayListResponse) {
        loadingDialog?.dismiss()
        res.items?.let {
            adapter.setData(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun requestFail(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }


    companion object {
        fun newInstance(header_title: String, playlistId: String): YoutubePlayListFragment {
            return YoutubePlayListFragment().apply {
                val bundle = Bundle()
                bundle.putString("header_title", header_title)
                bundle.putString("playlistId", playlistId)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: YoutubePlayListFragmentPresenter
    lateinit var adapter: YoutubePlayListAdapter
    var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_youtubeplaylist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(arguments?.getString("header_title") ?: "Youtube")
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        presenter = YoutubePlayListFragmentPresenter(this)

        context?.let {
            adapter = YoutubePlayListAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)

            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)

            loadingDialog?.show()
            val playlist = arguments?.getString("playlistId") ?: ""
            presenter.requestYoutubePlayList("snippet", "50", playlist, getString(R.string.google_youtube_key))
        }
    }
}