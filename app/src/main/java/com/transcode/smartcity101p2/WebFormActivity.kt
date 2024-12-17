package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.WebFormViewFragment
import com.transcode.smartcity101p2.model.Const

class WebFormActivity : CoreActivity() {

    lateinit var fragment: WebFormViewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_form)

        intent?.extras?.let {
            fragment = if (it.getString("endtime").isNullOrEmpty()) {
                val title = it.getString("title")
                val url = it.getString("url")
                val queue_type = it.getString("queue_type")
                val queue_id = it.getString("queue_id")
                val queue_title = it.getString("queue_title")
                WebFormViewFragment.newInstance(title, url, queue_type, queue_id, queue_title)
            } else {
                val title = it.getString("title")
                val url = it.getString("url")
                val queue_type = it.getString("queue_type")
                val queue_id = it.getString("queue_id")
                val date_string = it.getString("date_string")
                val queue_slot_id = it.getString("queue_slot_id")
                val base_lat = it.getString("base_lat")
                val base_lng = it.getString("base_lng")
                val queue_title = it.getString("queue_title")
                val endtime = it.getString("endtime")
                WebFormViewFragment.newInstance(title, url, queue_type, queue_id, date_string, queue_slot_id, base_lat, base_lng, queue_title, endtime)
            }
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Const.REQUEST_CODE_BOOK_QUEUE -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}