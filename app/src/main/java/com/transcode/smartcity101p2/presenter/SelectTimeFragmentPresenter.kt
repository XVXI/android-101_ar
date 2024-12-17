package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.SelectTimeFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.GetBookQueueNowResponse
import com.transcode.smartcity101p2.model.QueueSlotResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectTimeFragmentPresenter(var view: SelectTimeFragmentContract.View) : SelectTimeFragmentContract.Presenter {
    override fun bookQueue(queue_id: String, choose_datatime: String, citizen_id: String, queue_slot_id: String) {
        val callbacks = object : Callback<GetBookQueueNowResponse> {
            override fun onResponse(call: Call<GetBookQueueNowResponse>?, response: Response<GetBookQueueNowResponse>?) {
                response?.body()?.let {
                    if (it.code == "2201") {
                        view.bookSuccess(it.data[0])
                    } else {
                        view.showError(it.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetBookQueueNowResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        ApiRequest.INSTANCE.requestBook(callbacks, queue_id, choose_datatime, citizen_id, queue_slot_id, "")
    }

    override fun getQueueSlot(queue_id: String, choose_datetime: String) {
        val callbacks = object : Callback<QueueSlotResponse> {
            override fun onResponse(call: Call<QueueSlotResponse>?, response: Response<QueueSlotResponse>?) {
                response?.body()?.let {
                    if (it.data.size > 0) {
                        view.getQueueSlotSuccess(it.data)
                    }
                }
            }

            override fun onFailure(call: Call<QueueSlotResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        ApiRequest.INSTANCE.requestQueueSlot(callbacks, queue_id, choose_datetime)
    }
}