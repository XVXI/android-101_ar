package com.transcode.smartcity101p2.utils

import android.content.Context
import android.graphics.Typeface

enum class CustomFont(private var stringValue: String) {
    BAIJAMJUREE_REGULAR("BaiJamjuree-Regular"),
    KANIT_REGULAR("Kanit-Regular"),
    PSLXNARISSARA_BOLD("PSLxNarissara Bold"),
    SUKHUMVITSET("SukhumvitSet"),
    TF_LANNA("tf_lanna"),
    TF_LANNA_BOL("TF_Lanna_Bol"),
    TF_LANNA_ITA("TF_Lanna_Ita");

    override fun toString(): String = stringValue
}

class FontManager {

    companion object {
        fun getTypeFace(context: Context, font: CustomFont): Typeface {
            return Typeface.createFromAsset(context.assets, "fonts/$font.ttf")
        }
    }
}