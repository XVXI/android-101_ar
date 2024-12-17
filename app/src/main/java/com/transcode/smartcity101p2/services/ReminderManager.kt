package com.transcode.smartcity101p2.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.utils.AppUtils

class ReminderManager {
    private val milsec = 1000
    private val minit = milsec * 60
    private val hr = minit * 60

    private object Holder {
        val INSTANCE = ReminderManager()
    }

    companion object {
        val INSTANCE: ReminderManager by lazy {
            Holder.INSTANCE
        }
    }

    fun setAlert(queue: MyQueueResponse, context: Context): Int {

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.putExtra("queue_id", queue.QueueId.toString())

        val alertId = queue.QueueId.toString().toInt()
        val pendingIntent = PendingIntent.getBroadcast(context, alertId, alarmIntent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val selectDateMills = AppUtils.dateStringToMillis(queue.ChooseDatetime.toString(), arrayOf(AppUtils.formateDate2))

        val reminderTime = selectDateMills - hr

        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
        return alertId
    }
}