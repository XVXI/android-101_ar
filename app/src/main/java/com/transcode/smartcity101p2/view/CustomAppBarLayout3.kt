package com.transcode.smartcity101p2.view

import android.content.Context
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.util.AttributeSet
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import kotlinx.android.synthetic.main.appbar_main3.view.*

class CustomAppBarLayout3 : AppBarLayout {
    constructor(context: Context) : super(context) {
//        setHeaderColor()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        setHeaderColor()
    }

    private fun setHeaderColor() {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        loginResponse?.authority_info?.BgColor?.let {
            this.setBackgroundColor(Color.parseColor(it))
        }
    }

    fun setHeaderColor(color: Int) {
        this.setBackgroundColor(color)
    }

    fun setHeaderBackground(res: Int) {
        this.setBackgroundResource(res)
    }

    fun setTitle(title: String) {
        appBarContainer?.let {
            it.textTitle.text = title
            it.textTitle.isSelected = true
        }

        leftBt?.let {
            it.load("", R.mipmap.icon_back_new)
        }
    }

    fun setTitleColor(color: Int) {
        appBarContainer?.let {
            it.textTitle.setTextColor(color)
        }
    }
}