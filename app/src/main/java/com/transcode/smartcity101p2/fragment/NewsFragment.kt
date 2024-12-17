package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.NewsDetailActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.AllNewsAdapter
import com.transcode.smartcity101p2.contract.NewsFragmentContract
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.NewsImgResponse
import com.transcode.smartcity101p2.model.NewsResponse
import com.transcode.smartcity101p2.presenter.NewsFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_news.*


class NewsFragment : CoreFragment(), NewsFragmentContract.View, AllNewsAdapter.ClickItem {

    override fun updateImageList(news_id: String, list: ArrayList<NewsImgResponse.ImageData>) {
        imagecount += 1

        if (list.size > 0) {
            val imageList = arrayListOf<String>()
            for (data in list) {
                data.ImgUrl?.let {
                    imageList.add(it)
                }
            }
            for (data in adapter.list) {
                val id = data.NewsId ?: ""
                if (id == news_id && data.imgs == null) {
                    data.imgs = imageList
                }
            }
        }

        if (imagecount == adapter.list.size) {
            imagecount = 0
            adapter.notifyDataSetChanged()
        }
    }

    override fun onclickItem(res: NewsResponse) {
//        fragmentManager?.let {
//            FragmentHelper.replace(it, NewsDetailFragment.newInstance(res), R.id.content_home_frame)
//        }
        context?.apply {
            val intent = Intent(this, NewsDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(res)
            intent.putExtra("data", jsonData)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(header: String, news_cat_id: String): NewsFragment {
            return NewsFragment().apply {
                val bundle = Bundle()
                bundle.putString("header", header)
                bundle.putString("news_cat_id", news_cat_id)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: NewsFragmentPresenter
    lateinit var adapter: AllNewsAdapter
    var imagecount = 0
    var limit = 4
    var offset = 0
    var next_offset = 0
    var list_size = 0
    var list_total = 0
    var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = NewsFragmentPresenter(this)

        val header = arguments?.getString("header") ?: ""
        var news_cat_id = arguments?.getString("news_cat_id")
        if (news_cat_id.isNullOrEmpty()) {
            news_cat_id = null
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        loginResponse?.let {
            val appBar = appbar as CustomAppBarLayout
//            if (news_cat_id.isNullOrEmpty()) {
//            appBar.setTitle(it.authority_info?.CityName.toString())
//                appBar.leftBt.visibility = View.INVISIBLE
//                appBar.iconLeft.visibility = View.VISIBLE
//                appBar.iconLeft.load(it.authority_info?.LogoUrl, R.mipmap.ic_launcher)
//            } else {
            appBar.setTitle(header)
            appBar.leftBt.setOnClickListener {
                //                fragmentManager?.popBackStack()
                backPress()
            }
//            }
        }

        context?.let {
            photos_viewpager.visibility = View.GONE
            tab_layout.visibility = View.GONE

            adapter = AllNewsAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)

            recyclerview.addOnScrollListener(scrollListener)

            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)

        }

        presenter.getNews(news_cat_id, limit, offset)
    }

    override fun updateView(list: ArrayList<NewsResponse>) {
        loadingDialog?.dismiss()
        adapter.setData(list)
//        adapter.notifyDataSetChanged()

        if (list.size > 0) {
            text_empty.visibility = View.GONE
        } else {
            text_empty.visibility = View.VISIBLE
        }

        list_size = adapter.list.size
        list_total = adapter.total_news
        next_offset = offset + limit + 1

        for (news in adapter.list) {
            presenter.getNewsImage(news)
        }
    }

    override fun showError(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            if (message.isNotEmpty()) {
                Toast.makeText(it, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val isBottomReached = !recyclerView!!.canScrollVertically(1)
            if (isBottomReached && (offset < next_offset) && (list_size < list_total)) {
                offset += limit + 1
                loadingDialog?.show()
                var news_cat_id = arguments?.getString("news_cat_id")
                if (news_cat_id.isNullOrEmpty()) {
                    news_cat_id = null
                }
                presenter.getNews(news_cat_id, limit, offset)
            }
        }
    }

    fun backPress() {
        activity?.apply {
            finish()
        }
    }

    override fun onDestroy() {
//        Log.e("onDestroy", "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
//        FragmentHelper.remove(fragmentManager, this)
//        Log.e("onDestroyView", "onDestroyView")
        super.onDestroyView()
    }
}