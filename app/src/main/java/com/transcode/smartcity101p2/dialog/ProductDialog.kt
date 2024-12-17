package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.dialog_product.view.*

class ProductDialog(context: Context) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_product, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth() - 100, FrameLayout.LayoutParams.MATCH_PARENT)
        b?.dl_layout?.layoutParams = lp
        setContentView(b)

        b?.dl_close?.setOnClickListener { dismiss() }
    }

    fun getView(): View? = b

    fun setOnClickOKListener(onclickListener: View.OnClickListener) {
        b?.dl_product_add?.setOnClickListener { onclickListener.onClick(it) }
    }

    fun setOnClickCancelListener(onclickListener: View.OnClickListener) {
        b?.dl_product_cancel?.setOnClickListener { onclickListener.onClick(it) }
    }
}