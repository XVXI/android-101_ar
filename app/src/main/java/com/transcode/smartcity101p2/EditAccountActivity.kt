package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.EditAccountFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper

class EditAccountActivity : CoreActivity() {

    lateinit var fragment: EditAccountFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editacc)

        fragment = EditAccountFragment.newInstance()
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}