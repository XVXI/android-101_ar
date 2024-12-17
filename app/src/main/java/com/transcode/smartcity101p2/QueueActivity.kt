package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.QueueFragment

class QueueActivity : CoreActivity() {

    lateinit var fragment: QueueFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)

        fragment = QueueFragment.newInstance()
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}