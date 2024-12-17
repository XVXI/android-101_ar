package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.WebFormViewFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.GetBookQueueNowResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WebFormViewFragmentPresenter(var view: WebFormViewFragmentContract.View) : WebFormViewFragmentContract.Presenter {
    override fun bookNow(queue_id: String, citizen_id: String, form_id: String) {
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

        ApiRequest.INSTANCE.requestBookNow(callbacks, queue_id, citizen_id, form_id)
    }
}