package com.transcode.smartcity101p2

import android.os.Bundle
import com.google.gson.Gson
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.NewsDetailFragment
import com.transcode.smartcity101p2.model.NewsResponse

class NewsDetailActivity : CoreActivity() {

    lateinit var fragment: NewsDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        intent?.extras?.let {
            val gson = Gson()
            val data = gson.fromJson(it.getString("data"), NewsResponse::class.java)
            fragment = NewsDetailFragment.newInstance(data)
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.news_detail_content_home_frame)
        }
    }

    override fun onBackPressed() {
        fragment.backPress()
    }
}