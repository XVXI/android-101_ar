package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.LocationFragment

class LocationActivity : CoreActivity() {

    lateinit var fragment: LocationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        intent?.extras?.let {
            fragment = LocationFragment.newInstance(it.getDouble("lat"), it.getDouble("lng"))
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.location_content_home_frame)
        }
    }

    override fun onBackPressed() {
        fragment.backPress()
    }
}