package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.QueueDetailFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.GetBookQueueNowResponse
import com.transcode.smartcity101p2.model.QueueDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QueueDetailFragmentPresenter(var view: QueueDetailFragmentContract.View) : QueueDetailFragmentContract.Presenter {
    override fun getQueueDetail(queue_id: String) {
        val callbacks = object : Callback<ArrayList<QueueDetailResponse>> {
            override fun onResponse(call: Call<ArrayList<QueueDetailResponse>>?, response: Response<ArrayList<QueueDetailResponse>>?) {
                response?.body()?.let {
                    if (it.size > 0) {
                        view.updateView(it[0])
                    } else {
                        onFailure(call, Throwable())
                    }
                } ?: kotlin.run {
                    onFailure(call, Throwable())
                }
            }

            override fun onFailure(call: Call<ArrayList<QueueDetailResponse>>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        ApiRequest.INSTANCE.requestQueueDetail(callbacks, queue_id)
    }

    override fun bookNow(queue_id: String, citizen_id: String) {
        val callbacks = object : Callback<GetBookQueueNowResponse> {
            override fun onResponse(call: Call<GetBookQueueNowResponse>?, response: Response<GetBookQueueNowResponse>?) {
                response?.body()?.let {
                    if (it.code == "2201") {
                        view.bookSuccess(it.data[0])
                    } else {
                        view.showError(it.message.toString())
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<GetBookQueueNowResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        ApiRequest.INSTANCE.requestBookNow(callbacks, queue_id, citizen_id, "")
    }
}