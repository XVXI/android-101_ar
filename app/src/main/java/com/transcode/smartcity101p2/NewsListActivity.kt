package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.NewsFragment

class NewsListActivity : CoreActivity() {

    lateinit var fragment: NewsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        intent?.extras?.let {
            val title = it.getString("title")
            val cat_id = it.getString("cat_id")
            fragment = NewsFragment.newInstance(title, cat_id)
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.news_detail_content_home_frame)
        } ?: kotlin.run {
            fragment = NewsFragment.newInstance(getString(R.string.home_news), "")
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.news_detail_content_home_frame)
        }
    }

    override fun onBackPressed() {
        fragment.backPress()
    }
}