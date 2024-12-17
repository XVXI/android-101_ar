package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.CctvPlayerFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper

class CCtvPlayerActivity : CoreActivity() {

    lateinit var fragment: CctvPlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cctv_player)

        intent?.extras?.let {
            val stream_url = it.getString("stream_url")
            fragment = CctvPlayerFragment.newInstance(stream_url)
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
        }
    }
}