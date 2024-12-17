package com.transcode.smartcity101p2

import android.os.Bundle
import com.transcode.smartcity101p2.fragment.ChangePasswordFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_changepass.*
import kotlinx.android.synthetic.main.appbar_main2.view.*

class ChangePasswordActivity : CoreActivity() {

    lateinit var fragment: ChangePasswordFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepass)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.textTitle2.text = "เปลี่ยนรหัสผ่าน"
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            finish()
        }

        fragment = ChangePasswordFragment.newInstance()
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
    }
}