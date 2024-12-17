package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import kotlinx.android.synthetic.main.edit_dialog.view.*

class SelectEditDialog(context: Context) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.edit_dialog, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth() - 100, FrameLayout.LayoutParams.MATCH_PARENT)
        b?.select_edit_layout?.layoutParams = lp
        setContentView(b)

        b?.dl_close?.setOnClickListener { dismiss() }
    }

    fun getView(): View? = b

    fun setOnClickInfoListener(onclickListener: View.OnClickListener) {
        b?.dl_info?.setOnClickListener { onclickListener.onClick(it) }
    }

    fun setOnClickPasswordListener(onclickListener: View.OnClickListener) {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        if (loginResponse.authority_info?.IsFb == "1") {
            b?.dl_password?.visibility = View.GONE
        }else if (loginResponse.authority_info?.IsFb == "3") {
            b?.dl_password?.visibility = View.GONE
        }
        b?.dl_password?.setOnClickListener { onclickListener.onClick(it) }
    }
}