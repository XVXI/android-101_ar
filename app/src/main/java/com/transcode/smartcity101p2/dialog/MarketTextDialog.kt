package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.dialog_text.view.*

class MarketTextDialog(context: Context) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_text, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth() - 100, FrameLayout.LayoutParams.MATCH_PARENT)
        b?.dl_layout?.layoutParams = lp
        setContentView(b)

        b?.dl_bt?.setOnClickListener { dismiss() }
    }

    fun getView(): View? = b
}