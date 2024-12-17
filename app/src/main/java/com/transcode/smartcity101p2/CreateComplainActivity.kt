package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.CreateComplainFragment

class CreateComplainActivity : CoreActivity() {

    lateinit var fragment: CreateComplainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complain)

//        fragment = CreateComplainFragment.newInstance()
//        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}