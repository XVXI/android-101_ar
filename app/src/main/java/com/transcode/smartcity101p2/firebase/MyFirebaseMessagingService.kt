package com.transcode.smartcity101p2.firebase

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.notification.TrackNotificationManager
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.util.Log
import com.google.gson.JsonObject
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.services.UpdateNotification
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val NOTI_EMER = "NOTI_EMER"
        const val NOTI_COMPLAIN = "NOTI_COMPLAIN"
    }

    override fun onMessageReceived(rm: RemoteMessage?) {
        showNotification(rm)
    }

    private fun showNotification(rm: RemoteMessage?) {
        if (!Hawk.isBuilt()) {
            Hawk.init(baseContext).build()
        }

        val updateNotification = UpdateNotification()
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        updateNotification.updateComplain(login?.authority_info?.CitizenId ?: "", baseContext)
        updateNotification.updateEmergency(login?.authority_info?.CitizenId ?: "", baseContext)

        try {
            rm?.data?.let {
                val title = it["title"] ?: ""
                val message = it["body"] ?: ""
                val type = it["type"] ?: ""
                val data = it["data"] ?: ""
                val extra_data = it["extra_data"] ?: ""
                val status = it["status"] ?: ""
                val type_id = it["type_id"] ?: ""
                var type_name = typeIdtoName(type, type_id)

                when (type) {
                    "complain" -> {
//                        val intent = Intent()
//                        intent.action = Const.BROADCAST_TOGGLE_NOTIFICATION
//                        sendBroadcast(intent)
//                        updateComplainList(title, message, type, data, status, type_id, type_name)
                    }
                    "emergency" -> {
//                        val intent = Intent()
//                        intent.action = Const.BROADCAST_TOGGLE_NOTIFICATION
//                        sendBroadcast(intent)
//                        updateEmergencyList(title, message, type, data, status, type_id, type_name)
                    }
                    "complain_chat" -> {
//                        val json = it["extra_data"] ?: ""
//                        if (json.isNotEmpty()) {
//                            try {
//                                val jsonObject = JSONObject(json)
//                                val complain_id = jsonObject.getString("complain_id")
//                                updateComplainChat(complain_id)
//                            } catch (ex: Exception) {
//                            }
//                        }
//                        val intent = Intent()
//                        intent.action = Const.BROADCAST_TOGGLE_NOTIFICATION
//                        sendBroadcast(intent)
                    }
                    "emer_chat" -> {
//                        val json = it["extra_data"] ?: ""
//                        if (json.isNotEmpty()) {
//                            try {
//                                val jsonObject = JSONObject(json)
//                                val emer_id = jsonObject.getString("emer_id")
//                                updateEmerChat(emer_id)
//                            } catch (ex: Exception) {
//                            }
//                        }
//                        val intent = Intent()
//                        intent.action = Const.BROADCAST_TOGGLE_NOTIFICATION
//                        sendBroadcast(intent)
                    }
                }

                if (type.contains("ฉุกเฉิน")) {
                    val intent = Intent()
                    intent.action = Const.BROADCAST_EMERGENCY
                    sendBroadcast(intent)
                    updateComplainList(title, message, type, data, status, type_id, type_name)
                } else if (type.contains("ร้องเรียน")) {
                    val intent = Intent()
                    intent.action = Const.BROADCAST_COMPLAIN
                    sendBroadcast(intent)
                    updateEmergencyList(title, message, type, data, status, type_id, type_name)
                }

                TrackNotificationManager.INSTANCE.showNotification(this, title, message, type, data , extra_data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateEmerChat(emer_id: String) {
        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var d = hashMapOf<String, String>()
        var inside = false
        for (data in emer_db) {
            if (data["emer_id"] == emer_id) {
                inside = true
                d["emer_id"] = emer_id
                d["count"] = ((data["count"] ?: "0").toInt() + 1).toString()
                emer_db.remove(data)
                break
            }
        }

        if (!inside) {
            d["emer_id"] = emer_id
            d["count"] = "1"
        }

        emer_db.add(d)

        Hawk.delete(Const.EMER_CHAT_DB)
        Hawk.put(Const.EMER_CHAT_DB, emer_db)
    }

    private fun updateComplainChat(complain_id: String) {
        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var d = hashMapOf<String, String>()
        var inside = false
        for (data in complain_db) {
            if (data["complain_id"] == complain_id) {
                inside = true
                d["complain_id"] = complain_id
                d["count"] = ((data["count"] ?: "0").toInt() + 1).toString()
                complain_db.remove(data)
                break
            }
        }

        if (!inside) {
            d["complain_id"] = complain_id
            d["count"] = "1"
        }

        complain_db.add(d)

        Hawk.delete(Const.COMPLAIN_CHAT_DB)
        Hawk.put(Const.COMPLAIN_CHAT_DB, complain_db)
    }

    private fun typeIdtoName(type: String, type_id: String): String {
        var name = ""
        when (type) {
            "complain" -> {
                name = when (type_id) {
                    "1" -> getString(R.string.complain_complain_type_1)
                    "2" -> getString(R.string.complain_complain_type_2)
                    "3" -> getString(R.string.complain_complain_type_3)
                    "4" -> getString(R.string.complain_complain_type_4)
                    "5" -> getString(R.string.complain_complain_type_5)
                    "6" -> getString(R.string.complain_complain_type_6)
                    "7" -> getString(R.string.complain_complain_type_7)
                    "8" -> getString(R.string.complain_complain_type_8)
                    "9" -> getString(R.string.complain_complain_type_9)
                    else -> ""
                }
            }
            "emergency" -> {
                name = when (type_id) {
                    "1" -> getString(R.string.emergency_emergency_type_1)
                    "2" -> getString(R.string.emergency_emergency_type_2)
                    "3" -> getString(R.string.emergency_emergency_type_3)
                    "4" -> getString(R.string.emergency_emergency_type_4)
                    "5" -> getString(R.string.emergency_emergency_type_5)
                    "6" -> getString(R.string.emergency_emergency_type_6)
                    else -> ""
                }
            }
        }
        return name
    }

    private fun updateEmergencyList(title: String, message: String, type: String, data: String, status: String, type_id: String, type_name: String) {
        val notiList = if (Hawk.get<ArrayList<HashMap<String, String>>>(NOTI_EMER) != null) {
            Hawk.get<ArrayList<HashMap<String, String>>>(NOTI_EMER)
        } else {
            arrayListOf()
        }

        var position = -1
        for (i in 0 until notiList.size) {
            val item = notiList[i]
            if (type == "emergency") {
                if (item["data"] == data) {
                    position = i
                    break
                }
            }
        }

        if (position >= 0) {
            notiList.removeAt(position)
        }

        val hashMap = hashMapOf<String, String>()
        hashMap["title"] = title
        hashMap["message"] = message
        hashMap["type"] = type
        hashMap["data"] = data
        hashMap["status"] = status
        hashMap["type_id"] = type_id
        hashMap["type_name"] = type_name

        notiList.add(hashMap)

        Hawk.delete(NOTI_EMER)
        Hawk.put(NOTI_EMER, notiList)
    }

    private fun updateComplainList(title: String, message: String, type: String, data: String, status: String, type_id: String, type_name: String) {
        val notiList = if (Hawk.get<ArrayList<HashMap<String, String>>>(NOTI_COMPLAIN) != null) {
            Hawk.get<ArrayList<HashMap<String, String>>>(NOTI_COMPLAIN)
        } else {
            arrayListOf()
        }

        var position = -1
        for (i in 0 until notiList.size) {
            val item = notiList[i]
            if (type == "complain") {
                if (item["data"] == data) {
                    position = i
                    break
                }
            }
        }

        if (position >= 0) {
            notiList.removeAt(position)
        }

        val hashMap = hashMapOf<String, String>()
        hashMap["title"] = title
        hashMap["message"] = message
        hashMap["type"] = type
        hashMap["data"] = data
        hashMap["status"] = status
        hashMap["type_id"] = type_id
        hashMap["type_name"] = type_name

        notiList.add(hashMap)

        Hawk.delete(NOTI_COMPLAIN)
        Hawk.put(NOTI_COMPLAIN, notiList)
    }
}