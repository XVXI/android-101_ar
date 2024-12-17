package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.CctvListFragmentContract
import com.transcode.smartcity101p2.model.CctvRequest
import com.transcode.smartcity101p2.model.CctvResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CctvListFragmentPresenter(var view: CctvListFragmentContract.View) : CctvListFragmentContract.Presenter {
    override fun getCctvList(city_id: String, token: String) {
        val callbacks = object : Callback<CctvResponse> {
            override fun onResponse(call: Call<CctvResponse>?, response: Response<CctvResponse>?) {
                response?.body()?.data?.let {
                    view.updateList(it)
                } ?: kotlin.run {
                    view.updateList(arrayListOf())
                }
            }

            override fun onFailure(call: Call<CctvResponse>?, t: Throwable?) {
                view.updateList(arrayListOf())
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestGetCctv(callbacks, CctvRequest("", city_id, token), bearer)
    }
}