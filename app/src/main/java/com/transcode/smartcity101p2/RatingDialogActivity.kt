package com.transcode.smartcity101p2

import android.os.Bundle
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.contract.RatingDialogActivityContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.RatingDialogActivityPresenter
import kotlinx.android.synthetic.main.activity_rating_dialog.*

class RatingDialogActivity : CoreActivity(), RatingDialogActivityContract.View {
    override fun editSuccess() {
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    lateinit var presenter: RatingDialogActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_dialog)

        presenter = RatingDialogActivityPresenter(this)
        val queue_checkin_id = intent.extras.getString("queue_checkin_id")
        val title = intent.extras.getString("title")
        val titles = "ประเมินความพึงพอใจต่อการจองคิว $title"
        dl_title.text = titles
        dl_title.isSelected = true

        dl_close.setOnClickListener { finish() }
        dl_cancel.setOnClickListener { finish() }

        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val fname = login?.authority_info?.FName ?: ""
        val lname = login?.authority_info?.LName ?: ""
        val fullname = "$fname $lname"
        dl_name.text = fullname

        dl_ok.setOnClickListener {
            if (ratingBar.rating == 0f) {
                showError("กรุณาให้ rating")
                return@setOnClickListener
            } else if (dl_message.text.toString().isEmpty()) {
                showError(getString(R.string.hint_comment))
                return@setOnClickListener
            }
            presenter.editQueueCheckin(queue_checkin_id, ratingBar.rating.toString(), dl_message.text.toString())
        }
    }
}