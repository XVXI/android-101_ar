package com.transcode.smartcity101p2.services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.notification.UnreadComplainRequest
import com.transcode.smartcity101p2.model.notification.UnreadComplainResponse
import com.transcode.smartcity101p2.model.notification.UnreadEmerRequest
import com.transcode.smartcity101p2.model.notification.UnreadEmerResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateNotification {

    fun updateEmergency(citizen_id: String, context: Context) {
        if (!Hawk.isBuilt()) {
            Hawk.init(context).build()
        }
        val callbacks = object : Callback<UnreadEmerResponse> {
            override fun onResponse(call: Call<UnreadEmerResponse>?, response: Response<UnreadEmerResponse>?) {
                response?.body()?.data?.let {
                    for (emer in it) {
                        updateEmerChat(emer.emer_id ?: "", emer.unread ?: "0")
                    }
                    val intent = Intent()
                    intent.action = Const.BROADCAST_TOGGLE_NOTIFICATION
                    context.sendBroadcast(intent)
                }
            }

            override fun onFailure(call: Call<UnreadEmerResponse>?, t: Throwable?) {
                Log.e("re", "re")
            }
        }

        ApiRequest.INSTANCE.requestGetUnreadEmer(callbacks, UnreadEmerRequest(citizen_id))

    }

    fun updateComplain(citizen_id: String, context: Context) {
        if (!Hawk.isBuilt()) {
            Hawk.init(context).build()
        }
        val callbacks = object : Callback<UnreadComplainResponse> {
            override fun onResponse(call: Call<UnreadComplainResponse>?, response: Response<UnreadComplainResponse>?) {
                response?.body()?.data?.let {
                    for (complain in it) {
                        updateComplainChat(complain.complain_id ?: "", complain.unread ?: "0")
                    }
                    val intent = Intent()
                    intent.action = Const.BROADCAST_TOGGLE_NOTIFICATION
                    context.sendBroadcast(intent)
                }
            }

            override fun onFailure(call: Call<UnreadComplainResponse>?, t: Throwable?) {
                Log.e("re", "re")
            }
        }

        ApiRequest.INSTANCE.requestGetUnreadComplain(callbacks, UnreadComplainRequest(citizen_id))
    }

    private fun updateEmerChat(emer_id: String, count: String) {
        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var d = hashMapOf<String, String>()
        for (data in emer_db) {
            if (data["emer_id"] == emer_id) {
                emer_db.remove(data)
                break
            }
        }

        d["emer_id"] = emer_id
        d["count"] = count


        emer_db.add(d)

        Hawk.delete(Const.EMER_CHAT_DB)
        Hawk.put(Const.EMER_CHAT_DB, emer_db)
    }

    private fun updateComplainChat(complain_id: String, count: String) {
        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var d = hashMapOf<String, String>()
        for (data in complain_db) {
            if (data["complain_id"] == complain_id) {
                complain_db.remove(data)
                break
            }
        }

        d["complain_id"] = complain_id
        d["count"] = count

        complain_db.add(d)

        Hawk.delete(Const.COMPLAIN_CHAT_DB)
        Hawk.put(Const.COMPLAIN_CHAT_DB, complain_db)
    }
}