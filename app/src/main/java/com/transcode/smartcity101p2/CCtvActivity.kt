package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.CctvListFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper

class CCtvActivity : CoreActivity() {

    lateinit var fragment: CctvListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cctv)

        fragment = CctvListFragment.newInstance("CCTV")
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}