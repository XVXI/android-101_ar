package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.QueueDetailFragment
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.QueueListResponse

class QueueDetailActivity : CoreActivity() {

    lateinit var fragment: QueueDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_detail)

        intent?.extras?.let {
            val gson = Gson()
            val data = gson.fromJson(it.getString("data"), QueueListResponse::class.java)
            fragment = QueueDetailFragment.newInstance(data)
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