package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.transcode.smartcity101p2.fragment.BookQueueFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.model.Const

class BookQueueActivity : CoreActivity() {

    lateinit var fragment: BookQueueFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_queue)

        intent?.extras?.let {
            val queue_type = it.getString("queue_type")
            val queue_id = it.getString("queue_id")
            val choose_datatime = it.getString("choose_datatime")
            val serve_lat = it.getString("serve_lat")
            val serve_lng = it.getString("serve_lng")
            val queue_slot_id = it.getString("queue_slot_id")
            val queue_title = it.getString("queue_title")
            val endtime = it.getString("endtime")
            val form_id = it.getString("form_id")
            fragment = BookQueueFragment.newInstance(queue_type, queue_id, choose_datatime, serve_lat, serve_lng, queue_slot_id, queue_title, endtime, form_id)
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