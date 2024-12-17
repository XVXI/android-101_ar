package com.transcode.smartcity101p2.utils

import com.transcode.smartcity101p2.api.Services
import java.text.SimpleDateFormat
import java.util.*

class AppUtils {
    companion object {
        //        "2018-09-10T04:09:28.000Z"
        const val formateDate0 = "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'"
        const val formateDate1 = "yyyy-MM-dd"
        //        2018-09-10 11:10:04
        const val formateDate2 = "yyyy-MM-dd HH:mm:ss"

        const val formateDate3 = "dd MMM yyyy"
        const val formateDate4 = "HH:mm"
        const val formateDate8 = "HH:mm:ss"

        const val formateDate5 = "$formateDate3 $formateDate4"
        const val formateDate6 = "$formateDate1 $formateDate4"
        const val formateDate7 = "$formateDate3 $formateDate8"

        const val milsec = 1000
        const val minute = milsec * 60
        const val hr = minute * 60
        const val day = hr * 24

        fun getDateString(formatString: String, dateMillis: Long): String {
            val local = Locale("th", "TH")
            val sdf = SimpleDateFormat(formatString, local)
            return sdf.format(dateMillis)
        }

        fun getDateStringBD(formatString: String, dateMillis: Long): String {
            val local = Locale("th", "TH")
            val sdf = SimpleDateFormat(formatString, local)
            val date = Date(dateMillis)
            val cal = GregorianCalendar.getInstance()
            cal.time = date
            Calendar.MONDAY
            return sdf.format(dateMillis)


        }

        fun dateStringToMillis(dateString: String, formatString: Array<String>): Long {
            val local = Locale("th", "TH")
            for (formatStrings in formatString) {
                try {
                    val formatter = SimpleDateFormat(formatStrings, local)
                    val date = formatter.parse(dateString)

                    return date.time

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return 0
        }

        fun getImageUrl(url: String?): String {
            url?.let {
                val imageUrl = if (it.startsWith("http") || it.isEmpty()) {
                    it
                } else {
                    Services.BASE_URL + "/getimg?code=" + it
                }
                return imageUrl
            } ?: kotlin.run {
                return ""
            }
        }
    }
}