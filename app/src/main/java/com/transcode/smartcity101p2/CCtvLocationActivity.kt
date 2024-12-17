package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.CctvLocationFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper

class CCtvLocationActivity : CoreActivity() {

    lateinit var fragment: CctvLocationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cctv_location)

        intent?.extras?.let {
            val lat = it.getDouble("lat")
            val lng = it.getDouble("lng")
            val header_title = it.getString("header_title")
            val place_title = it.getString("place_title")
            val stream_url = it.getString("stream_url")
            fragment = CctvLocationFragment.newInstance(lat, lng, header_title, place_title, stream_url)
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
        }
    }
}