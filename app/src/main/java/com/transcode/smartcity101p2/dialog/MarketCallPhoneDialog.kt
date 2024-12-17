package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.dialog_market_callphone.view.*

class MarketCallPhoneDialog(context: Context) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_market_callphone, null, false)
        setContentView(b)

        b?.dl_close?.setOnClickListener { dismiss() }
    }

    fun setMessage(message: String) {
        b?.dl_message?.text = message
    }

    fun setTitle(title: String) {
        b?.dl_title?.text = title
    }

    fun setOnClickOKListener(onclickListener: View.OnClickListener) {
        b?.dl_ok?.setOnClickListener { onclickListener.onClick(it) }
    }

    fun setOnClickCancelListener(onclickListener: View.OnClickListener) {
        b?.dl_cancel?.setOnClickListener { onclickListener.onClick(it) }
    }
}