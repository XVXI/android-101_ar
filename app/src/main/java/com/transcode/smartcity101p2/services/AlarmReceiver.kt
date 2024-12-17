package com.transcode.smartcity101p2.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.notification.TrackNotificationManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //show notification
        context?.let {
            if (!Hawk.isBuilt()) {
                Hawk.init(it).build()
            }
            val queue = Hawk.get<MyQueueResponse>(intent?.getStringExtra("queue_id"))
            if (queue != null) {
                Hawk.delete(intent?.getStringExtra("queue_id"))
                val gson = GsonBuilder().create()
                val jsonData = gson.toJson(queue)
                TrackNotificationManager.INSTANCE.showNotification(it, queue.QueueName.toString(), "ใกล้ถึงคิวของท่านแล้ว", "queue", jsonData, "")
            }
        }
    }
}