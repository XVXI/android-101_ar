package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.AccountFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragmentPresenter(var view: AccountFragmentContract.View) : AccountFragmentContract.Presenter {
    override fun getCitizenInfo() {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val callbacks = object : Callback<CitizenInfoResponse> {
            override fun onResponse(call: Call<CitizenInfoResponse>?, response: Response<CitizenInfoResponse>?) {
                response?.body()?.data?.let {
                    it.CitizenId?.apply {
                        view.updateCitizenInfo(it)
                    }
                }
            }

            override fun onFailure(call: Call<CitizenInfoResponse>?, t: Throwable?) {
//                Log.e("re", "re")
            }
        }
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestCitizenInfo(callbacks, CitizenInfoRequest(citizen_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }

    override fun getCityFunction() {
        val callbacks = object : Callback<AllCityFunctionResponse> {
            override fun onResponse(call: Call<AllCityFunctionResponse>?, response: Response<AllCityFunctionResponse>?) {
                response?.body()?.data?.let {
                    view.updateList(it)
                }
            }

            override fun onFailure(call: Call<AllCityFunctionResponse>?, t: Throwable?) {
//                Log.e("re", "re")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var city_id = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestCityFunction(callbacks, CityFunctionRequest(city_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }
}