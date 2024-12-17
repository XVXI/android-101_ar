package com.transcode.smartcity101p2.view

import android.content.Context
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import com.transcode.smartcity101p2.utils.CustomFont
import com.transcode.smartcity101p2.utils.FontManager

class KanitTextInputLayout : TextInputLayout {
    constructor(context: Context) : super(context) {
        setFont()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setFont()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setFont()
    }

    private fun setFont() {
        context?.let {
            this.setTypeface(FontManager.getTypeFace(it, CustomFont.TF_LANNA))
        }
    }
}