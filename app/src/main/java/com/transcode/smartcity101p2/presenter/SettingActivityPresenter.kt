package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.SettingActivityContract
import com.transcode.smartcity101p2.model.CitizenInfoRequest
import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingActivityPresenter(var view: SettingActivityContract.View) : SettingActivityContract.Presenter {
    override fun getCitizenInfo() {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val callbacks = object : Callback<CitizenInfoResponse> {
            override fun onResponse(call: Call<CitizenInfoResponse>?, response: Response<CitizenInfoResponse>?) {
                response?.body()?.data?.let {
                    it.CitizenId?.apply {
                        view.updateCitizenInfo(it)
                    } ?: kotlin.run {
                        view.onFail()
                    }
                } ?: kotlin.run {
                    view.onFail()
                }
            }

            override fun onFailure(call: Call<CitizenInfoResponse>?, t: Throwable?) {
//                Log.e("re", "re")
                view.onFail()
            }
        }
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestCitizenInfo(callbacks, CitizenInfoRequest(citizen_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }
}