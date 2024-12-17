package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.RatingDialogActivityContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.EditQueueCheckinRequest
import com.transcode.smartcity101p2.model.EditQueueCheckinResponse
import com.transcode.smartcity101p2.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatingDialogActivityPresenter(var view: RatingDialogActivityContract.View) : RatingDialogActivityContract.Presenter {
    override fun editQueueCheckin(queue_checkin_id: String, rate: String, comment: String) {
        val callbacks = object : Callback<EditQueueCheckinResponse> {
            override fun onResponse(call: Call<EditQueueCheckinResponse>?, response: Response<EditQueueCheckinResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.showError("Success")
                        view.editSuccess()
                    } else {
                        view.showError(it.message.toString())
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<EditQueueCheckinResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        var token = loginResponse.authority_info?.getAllToken() ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val request = EditQueueCheckinRequest(token, queue_checkin_id, rate, comment)
        ApiRequest.INSTANCE.requestEditQueueCheckin(callbacks, request, bearer)
    }
}