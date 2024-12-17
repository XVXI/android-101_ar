package com.transcode.smartcity101p2.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.transcode.smartcity101p2.utils.CustomFont
import com.transcode.smartcity101p2.utils.FontManager

class KanitTextView : TextView {
    constructor(context: Context) : super(context) {
        setFont()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setFont()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setFont()
    }

    fun setFont() {
        context?.let {
            this.typeface = FontManager.getTypeFace(it, CustomFont.TF_LANNA)
        }
    }
}