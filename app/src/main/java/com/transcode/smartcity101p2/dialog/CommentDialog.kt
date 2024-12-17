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
import kotlinx.android.synthetic.main.comment_dialog.view.*

class CommentDialog(context: Context) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.comment_dialog, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth() - 100, FrameLayout.LayoutParams.MATCH_PARENT)
        b?.comment_layout?.layoutParams = lp
        setContentView(b)

        b?.dl_close?.setOnClickListener { dismiss() }

        val acc = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        acc?.let {
            b?.dl_name?.text = it.authority_info?.FName + " " + it.authority_info?.LName
        }
    }

    fun hideRating() {
        b?.layout_rating?.visibility = View.GONE
    }

    fun getView(): View? = b

    fun setOnClickOKListener(onclickListener: View.OnClickListener) {
        b?.dl_ok?.setOnClickListener { onclickListener.onClick(it) }
    }

    fun setOnClickCancelListener(onclickListener: View.OnClickListener) {
        b?.dl_cancel?.setOnClickListener { onclickListener.onClick(it) }
    }
}