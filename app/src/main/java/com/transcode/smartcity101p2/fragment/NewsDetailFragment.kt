package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.NewsDetailAdapter
import com.transcode.smartcity101p2.adapter.PhotosAdapter
import com.transcode.smartcity101p2.contract.NewsDetailFragmentContract
import com.transcode.smartcity101p2.dialog.CommentDialog
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.presenter.NewsDetailFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.comment_dialog.view.*
import kotlinx.android.synthetic.main.fragment_news_details.*

class NewsDetailFragment : CoreFragment(), NewsDetailFragmentContract.View, PhotosAdapter.ClickItem {

    companion object {
        fun newInstance(data: NewsResponse): NewsDetailFragment {
            return NewsDetailFragment().apply {
                val bundle = Bundle()
                val gson = GsonBuilder().create()
                val jsonData = gson.toJson(data)
                bundle.putString("data", jsonData)
                arguments = bundle
            }
        }
    }

    private lateinit var newsResponse: NewsResponse
    lateinit var adapter: NewsDetailAdapter
    lateinit var presenter: NewsDetailFragmentPresenter
    var newsFileList = arrayListOf<NewsFileResponse.NewsFileResponseData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        bundle?.let {
            if (it.containsKey("data")) {
                val gson = Gson()
                newsResponse = gson.fromJson(it.getString("data"), NewsResponse::class.java)
            }
        }
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(newsResponse.NewsTitle.toString())
        appBar.leftBt.setOnClickListener {
            if (frame_details.visibility == View.GONE) {
                onClickItem()
            } else {
//                fragmentManager?.popBackStack()
                activity?.apply {
                    finish()
                }
            }
        }

        presenter = NewsDetailFragmentPresenter(this)

        context?.let {
            adapter = NewsDetailAdapter(it, fragmentManager)
            adapter.setRecyclerView(frame_details)
            val ar = arrayListOf<Any>()
            val hashMap = hashMapOf<String, String>()
            hashMap["title"] = newsResponse.NewsTitle.toString()
            hashMap["data"] = newsResponse.NewsDetail.toString()
            hashMap["rating"] = "0"
            hashMap["lat"] = newsResponse.NewsIat
            hashMap["lng"] = newsResponse.NewsIng
            hashMap["date"] = newsResponse.CreateDateTime.toString()
            hashMap["cat"] = newsResponse.newsCatName.toString()
            hashMap["Day"] = newsResponse.Day.toString()
            hashMap["Time"] = newsResponse.Time.toString()
            ar.add(hashMap)
            adapter.setData(ar)
            adapter.notifyDataSetChanged()

            val token = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)?.authority_info?.getAllToken()
                    ?: ""
            val news_id = newsResponse?.NewsId ?: ""

            presenter.getNewsFile(token, news_id)
        }

        if (Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)?.authority_info?.IsFb == "2") {
            add_comment.visibility = View.GONE
        }

        add_comment.setOnClickListener { showCommentDialog() }

        context?.let {
            val photoAdapter = PhotosAdapter(it)
            photoAdapter.setClickItemListener(this)
            photos_viewpager.adapter = photoAdapter
            newsResponse.imgs?.let {
                val list = arrayListOf<String>()
                for (n in it) {
                    n?.let {
                        if (it.startsWith("http")) list.add(n)
                    }
                }

                if (list.size <= 0) {
                    frame_image.visibility = View.GONE
                }

                photoAdapter.setData(list)
                photoAdapter.notifyDataSetChanged()
                tab_layout.setupWithViewPager(photos_viewpager, true)
                photos_viewpager.setCurrentItem(0, false)
            } ?: kotlin.run {
                frame_image.visibility = View.GONE
            }

            for (i in 0 until photoAdapter.count step 1) {
                val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab2, null)
                tab.setBackgroundResource(R.drawable.tab_selector)
                tab_layout.getTabAt(i)?.customView = tab
                tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(it.resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
            }
            photos_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    if (position >= photoAdapter.count) {
                        photos_viewpager?.setCurrentItem(0, false)
                    } else {
                        photos_viewpager?.setCurrentItem(position, true)
                    }
                }

            })
        }

        autoScroll()
    }

    fun backPress() {
        val appBar = appbar as CustomAppBarLayout
        appBar.leftBt.performClick()
    }

    private val handler = Handler()
    private val autoScrollRunnable = Runnable {
        photos_viewpager?.let {
            val nextItem = if (it.currentItem + 1 < it.adapter?.count ?: 0) {
                it.currentItem + 1
            } else {
                0
            }
            it.setCurrentItem(nextItem, true)
            autoScroll()
        }
    }

    private fun autoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
        handler.postDelayed(autoScrollRunnable, 2500)
    }

    override fun onClickItem() {
        when (frame_details.visibility) {
            View.GONE -> {
                frame_details.visibility = View.VISIBLE
                frame_image.visibility = View.VISIBLE
                add_comment.visibility = View.VISIBLE

                frame_image0.visibility = View.GONE
            }
            View.VISIBLE -> {
                frame_details.visibility = View.GONE
                frame_image.visibility = View.GONE
                add_comment.visibility = View.GONE

                frame_image0.visibility = View.VISIBLE

                context?.let {
                    val photoAdapter = PhotosAdapter(it)
                    photoAdapter.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    photos_viewpager0.adapter = photoAdapter
                    newsResponse.imgs?.let {
                        val list = arrayListOf<String>()
                        for (n in it) {
                            n?.let {
                                if (it.startsWith("http")) list.add(n)
                            }
                        }
                        photoAdapter.setData(list)
                        photoAdapter.notifyDataSetChanged()
                        tab_layout0.setupWithViewPager(photos_viewpager0, true)
                    }

                    for (i in 0 until photoAdapter.count step 1) {
                        val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab2, null)
                        tab.setBackgroundResource(R.drawable.tab_selector)
                        tab_layout0.getTabAt(i)?.customView = tab
                        tab_layout0.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(it.resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
                    }
                }
            }
        }
        if (Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)?.authority_info?.IsFb == "2") {
            add_comment.visibility = View.GONE
        }
    }

    private fun showCommentDialog() {
        context?.let {
            val commentDialog = CommentDialog(it)
            commentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            commentDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            commentDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            commentDialog.show()
            commentDialog.setOnClickOKListener(View.OnClickListener {
                val res = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                val view = commentDialog.getView()
                res?.let {
                    if (view?.ratingBar?.rating == 0f) {
                        showError("กรุณาให้ rating")
                        return@OnClickListener
                    } else if (view?.dl_message?.text.toString().isEmpty()) {
                        showError(getString(R.string.hint_comment))
                        return@OnClickListener
                    }
                    presenter.addComment(it?.authority_info?.getAllToken()
                            ?: "", newsResponse?.NewsId
                            ?: "", view?.dl_message?.text.toString(), view?.ratingBar?.rating.toString(), it.authority_info?.CitizenId
                            ?: "")
                }
                commentDialog.dismiss()
            })
            commentDialog.setOnClickCancelListener(View.OnClickListener {
                commentDialog.dismiss()
            })
        }
    }

    override fun updateView(lits: ArrayList<AllNewsCommentResponse.AllNewsCommentResponseItem>) {
        val data_list = arrayListOf<Any>()
        val hashMap = hashMapOf<String, String>()
        if (lits.size > 0) {
            var rate_sum = 0.0
            for (n in lits) {
                val rate = n.Rate?.toFloatOrNull()
                if (rate != null) {
                    rate_sum += rate
                }
            }
            val num = Math.round(rate_sum / lits.size)
            hashMap["title"] = newsResponse.NewsTitle.toString()
            hashMap["data"] = newsResponse.NewsDetail.toString()
            hashMap["lat"] = newsResponse.NewsIat
            hashMap["lng"] = newsResponse.NewsIng
            hashMap["date"] = newsResponse.CreateDateTime.toString()
            hashMap["cat"] = newsResponse.newsCatName.toString()
            hashMap["Day"] = newsResponse.Day.toString()
            hashMap["Time"] = newsResponse.Time.toString()
            hashMap["rating"] = num.toString()
            val gson = GsonBuilder().create()
            val listdata = gson.toJson(newsFileList)
            hashMap["files"] = listdata
            data_list.add(hashMap)
            data_list.add("header")
            data_list.addAll(lits)
        } else {
            hashMap["title"] = newsResponse.NewsTitle.toString()
            hashMap["data"] = newsResponse.NewsDetail.toString()
            hashMap["lat"] = newsResponse.NewsIat
            hashMap["lng"] = newsResponse.NewsIng
            hashMap["date"] = newsResponse.CreateDateTime.toString()
            hashMap["cat"] = newsResponse.newsCatName.toString()
            hashMap["Day"] = newsResponse.Day.toString()
            hashMap["Time"] = newsResponse.Time.toString()
            hashMap["rating"] = "0.0"
            val gson = GsonBuilder().create()
            val listdata = gson.toJson(newsFileList)
            hashMap["files"] = listdata
            data_list.add(hashMap)
        }
        adapter.setData(data_list)
        adapter.notifyDataSetChanged()
    }

    override fun updateNewsFile(list: ArrayList<NewsFileResponse.NewsFileResponseData>) {
        newsFileList = list
        val token = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)?.authority_info?.getAllToken()
                ?: ""
        val news_id = newsResponse?.NewsId ?: ""

        presenter.getComment(token, news_id)
    }

    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }
}