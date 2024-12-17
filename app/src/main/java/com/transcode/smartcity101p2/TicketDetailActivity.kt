package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.TicketDetailFragment
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.MyQueueResponse

class TicketDetailActivity : CoreActivity() {

    lateinit var fragment: TicketDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_detail)

        intent?.extras?.let {
            val gson = Gson()
            val data = gson.fromJson(it.getString("data"), MyQueueResponse::class.java)
            fragment = TicketDetailFragment.newInstance(data)
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

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }
}