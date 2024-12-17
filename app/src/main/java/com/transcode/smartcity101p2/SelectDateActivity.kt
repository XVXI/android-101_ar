package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.SelectDateFragment
import com.transcode.smartcity101p2.model.Const

class SelectDateActivity : CoreActivity() {

    lateinit var fragment: SelectDateFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)

        intent?.extras?.let {
            fragment = if (it.getBoolean("isEdit")) {
                val queue_id = it.getString("queue_id")
                val queue_checkin_id = it.getString("queue_checkin_id")
                val queue_title = it.getString("queue_title")
                SelectDateFragment.newInstance(queue_id, queue_checkin_id, queue_title)
            } else {
                val queue_id = it.getString("queue_id")
                val queue_type = it.getString("queue_type")
                val form_url = it.getString("form_url")
                val base_lat = it.getString("base_lat")
                val base_lng = it.getString("base_lng")
                val queue_title = it.getString("queue_title")
                SelectDateFragment.newInstance(queue_id, queue_type, form_url, base_lat, base_lng, queue_title)
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