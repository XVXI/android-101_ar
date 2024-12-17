package com.transcode.smartcity101p2.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationChannel
import android.graphics.BitmapFactory
import android.os.Build
import com.transcode.smartcity101p2.ResultActivity
import android.app.ActivityManager
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse


const val KEY_NOTIFICATION_ID = "notification_id"

class TrackNotificationManager {

    private object Holder {
        val INSTANCE = TrackNotificationManager()
    }

    companion object {
        val INSTANCE: TrackNotificationManager by lazy {
            Holder.INSTANCE
        }
    }

    fun showNotification(context: Context, title: String, message: String, type: String, data: String, extra_data: String) {
        if (!Hawk.isBuilt()) {
            Hawk.init(context).build()
        }
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        if (login.authority_info?.CitizenId.isNullOrEmpty()) {
            return
        }

        val notifyID = (System.currentTimeMillis() and 0xfffffff).toInt()
        val CHANNEL_ID = "citizen_channel_01"// The id of the channel.
        val name = "citizen_channel_01"// The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_HIGH

        val nextIntent = Intent(context, ResultActivity::class.java)
        nextIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        nextIntent.putExtra(KEY_NOTIFICATION_ID, notifyID.toString())
        nextIntent.putExtra("type", type)
        nextIntent.putExtra("data", data)
        nextIntent.putExtra("extra_data", extra_data)
        val pendingIntent = PendingIntent.getActivity(context, notifyID, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

//        contentView.setOnClickPendingIntent(R.id.snackbar_action, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification_count = getNotificationCount()
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.setShowBadge(true)
            val notification = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.icon_app)
                    .setBadgeIconType(R.mipmap.icon_app)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.icon_app))
                    .setVibrate(longArrayOf(500, 500, 500, 500, 500))
                    .setLights(Color.GREEN, 3000, 3000)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setNumber(notification_count)
                    .build()
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.createNotificationChannel(mChannel)
            mNotificationManager.notify(notifyID, notification)
        } else {
            val notification = NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.icon_app)
                    .setBadgeIconType(R.mipmap.icon_app)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.icon_app))
                    .setVibrate(longArrayOf(500, 500, 500, 500, 500))
                    .setLights(Color.GREEN, 3000, 3000)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build()

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notifyID, notification)
        }
        val notificationIdList = if (Hawk.get<ArrayList<Int>>("noti_id_list") != null) {
            Hawk.get<ArrayList<Int>>("noti_id_list")
        } else {
            arrayListOf()
        }
        notificationIdList.add(notifyID)
        Hawk.delete("noti_id_list")
        Hawk.put("noti_id_list", notificationIdList)

    }

    private fun isRunning(ctx: Context): Boolean {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(Integer.MAX_VALUE)

        for (task in tasks) {
            if (ctx.packageName.equals(task.baseActivity.packageName, ignoreCase = true))
                return true
        }

        return false
    }

    private fun getNotificationCount(): Int {
        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var complain_count = 0

        for (data in complain_db) {
            complain_count += (data["count"] ?: "0").toInt()
        }

        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var emer_count = 0

        for (data in emer_db) {
            emer_count += (data["count"] ?: "0").toInt()
        }
        return complain_count + emer_count
    }
}