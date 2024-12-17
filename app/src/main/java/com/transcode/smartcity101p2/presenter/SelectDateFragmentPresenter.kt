package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.SelectDateFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.HashMap

class SelectDateFragmentPresenter(var view: SelectDateFragmentContract.View) : SelectDateFragmentContract.Presenter {
    private val dayTimeMills = 60 * 60 * 24 * 1000
    private val list = arrayListOf<HashMap<String, Any>>()

    override fun calculateDayList(queue_type: String) {
        list.clear()
        var start = 1
        var stop = 7
        if (queue_type == "3") {
            start = 2
            stop = 8
        }

        for (i in start..stop) {
            val h = HashMap<String, Any>()
            val date = System.currentTimeMillis() + (dayTimeMills * i)
            h["date"] = date
            h["selected"] = false

            list.add(h)
        }
        view.updateView(list)
    }

    override fun getQueueSlot(groupPosition: Int, queue_id: String, choose_datetime: String) {
        val callbacks = object : Callback<QueueSlotResponse> {
            override fun onResponse(call: Call<QueueSlotResponse>?, response: Response<QueueSlotResponse>?) {
                response?.body()?.let {
                    view.getQueueSlotSuccess(it.data, groupPosition)
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<QueueSlotResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        ApiRequest.INSTANCE.requestQueueSlot(callbacks, queue_id, choose_datetime)
    }

    override fun editChooseDatetime(queue_checkin_id: String, choose_datetime: String, queue_slot_id: String) {
        val callbacks = object : Callback<EditChooseDatetimeResponse> {
            override fun onResponse(call: Call<EditChooseDatetimeResponse>?, response: Response<EditChooseDatetimeResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.editChooseDatetimeSuccess()
                    } else {
                        view.showError(it.message ?: Const.MESSAGE_BOOK_ERROR)
                    }
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<EditChooseDatetimeResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_BOOK_ERROR)
            }
        }
        val request = EditChooseDatetimeRequest(queue_checkin_id, choose_datetime, queue_slot_id)
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestEditChooseDatetime(callbacks, request, bearer)
    }

}