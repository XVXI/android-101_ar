package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TicketDetailFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TicketDetailFragmentPresenter(var view: TicketDetailFragmentContract.View) : TicketDetailFragmentContract.Presenter {
    override fun cancelQueueCheckin(queue_checkin_id: String) {
        val callbacks = object : Callback<CancelQueueCheckinResponse> {
            override fun onResponse(call: Call<CancelQueueCheckinResponse>?, response: Response<CancelQueueCheckinResponse>?) {
                response?.body()?.let {
                    if(it.code == "1" || it.code == "200"){
                        view.cancelSuccess()
                    }else{
                        view.showError(Const.MESSAGE_ERROR)
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<CancelQueueCheckinResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }
        val request = CancelQueueCheckinRequest(queue_checkin_id)
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestCancelQueueCheckinRequest(callbacks, request, bearer)
    }

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
}