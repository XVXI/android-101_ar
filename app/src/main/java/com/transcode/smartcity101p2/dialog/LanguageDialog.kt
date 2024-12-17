package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.dialog_lang.view.*

class LanguageDialog(context: Context) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_lang, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth(), FrameLayout.LayoutParams.WRAP_CONTENT)
        b?.dl_layout?.layoutParams = lp
        setContentView(b)

        b?.b_bar?.visibility = View.GONE

        if (hasNavBar()) {
            val lp2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getNavigationH())
            b?.b_bar?.visibility = View.VISIBLE
            b?.b_bar?.layoutParams = lp2
        }
    }

    fun getView(): View? = b

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result;
    }

    private fun hasNavBar(): Boolean {
        val resources = context.resources
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    private fun getNavigationH(): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }
}