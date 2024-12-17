package com.transcode.smartcity101p2

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_info_layout.*
import kotlinx.android.synthetic.main.appbar_main2.view.*

class InfoActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_layout)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.textTitle2.text = "บัญชีผู้ใช้"
        appBar.setHeaderBackground(R.drawable.market_bg_gradient)
        appBar.leftBt.setOnClickListener {
            finish()
        }
        appBar.leftBt.load("", R.mipmap.market_back_icon)

        update_info.setOnClickListener {
            startActivity(Intent(this, EditAccountActivity::class.java))
        }

        change_password.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        checkUserType()
    }

    private fun checkUserType() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        when {
            user.authority_info?.IsFb == "2" -> {
                update_info.visibility = View.GONE
                change_password.visibility = View.GONE
            }
            user.authority_info?.IsFb == "1" -> {
                update_info.visibility = View.VISIBLE
                change_password.visibility = View.GONE
            }
            user.authority_info?.IsFb == "3" -> {
                update_info.visibility = View.VISIBLE
                change_password.visibility = View.GONE
            }
            else -> {
                update_info.visibility = View.VISIBLE
                change_password.visibility = View.VISIBLE
            }
        }
    }
}