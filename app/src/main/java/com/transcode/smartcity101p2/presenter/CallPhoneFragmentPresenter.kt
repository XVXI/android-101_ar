package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.CallPhoneFragmentContract
import com.transcode.smartcity101p2.model.CallPhoneRequest
import com.transcode.smartcity101p2.model.CallPhoneResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallPhoneFragmentPresenter(var view: CallPhoneFragmentContract.View) : CallPhoneFragmentContract.Presenter {
    override fun getCallPhoneNumber(city_id: String, token: String) {
        val callbacks = object : Callback<CallPhoneResponse> {
            override fun onResponse(call: Call<CallPhoneResponse>?, response: Response<CallPhoneResponse>?) {
                response?.body()?.data?.let {
                    view.updateList(it)
                } ?: kotlin.run {
                    view.updateList(arrayListOf())
                }
            }

            override fun onFailure(call: Call<CallPhoneResponse>?, t: Throwable?) {
                view.updateList(arrayListOf())
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestGetCall(callbacks, CallPhoneRequest("", city_id, token), bearer)
    }
}