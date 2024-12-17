package com.transcode.smartcity101p2.extension

import android.widget.TextView
import com.transcode.smartcity101p2.utils.AppUtils

fun TextView.ConvertDate(dateString: String) {
    val dateMillis = AppUtils.dateStringToMillis(dateString, arrayOf(AppUtils.formateDate0))
    val date = AppUtils.getDateString(AppUtils.formateDate3, dateMillis)
    this.text = date
}