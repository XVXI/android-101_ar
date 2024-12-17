package com.transcode.smartcity101p2.extension

import com.transcode.smartcity101p2.BuildConfig
import com.transcode.smartcity101p2.utils.AppUtils
import java.security.MessageDigest

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digested = md.digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}

fun String.concurrencyFormat(): String {
    var returnString = ""
    if (length > 0) {
        var digit = 0
        for (i in (length - 1) downTo 0) {
            digit += 1
            if (digit >= 3 && (i - 1 >= 0)) {
                digit = 0
                returnString = "," + get(i) + returnString
            } else {
                returnString = get(i) + returnString
            }
        }
    }
    return returnString
}

fun String.convertDate(): String {
    val timeMills = AppUtils.dateStringToMillis(this, arrayOf(AppUtils.formateDate0))
    val con = AppUtils.getDateString(AppUtils.formateDate5, timeMills)
    return con
}

fun String.convertDate(plus: Long): String {
    val timeMills = AppUtils.dateStringToMillis(this, arrayOf(AppUtils.formateDate0))
    val con = AppUtils.getDateString(AppUtils.formateDate5, timeMills + plus)
    return con
}

fun String.convertProductImage(): String {
    return BuildConfig.BASE_URL + this
}