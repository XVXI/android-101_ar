package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.CallPhoneFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper

class CallPhoneActivity : CoreActivity() {

    lateinit var fragment: CallPhoneFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_phone)

        fragment = CallPhoneFragment.newInstance(getString(R.string.home_hotline))
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }

    override fun onBackPressed() {
        fragment.backPress()
    }
}