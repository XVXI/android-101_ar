package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.CreateEmergencyFragment

class CreateEmergencyActivity : CoreActivity() {

    lateinit var fragment: CreateEmergencyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_emergency)

//        fragment = CreateEmergencyFragment.newInstance()
//        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}