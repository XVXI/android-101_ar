package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketMyFragmentContract
import com.transcode.smartcity101p2.model.CitizenInfoRequest
import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.request.AddressRequest
import com.transcode.smartcity101p2.model.buyapi.request.AddressRequestQ
import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketMyFragmentPresenter(var view: MarketMyFragmentContract.View) : MarketMyFragmentContract.Presenter {
    override fun getAllAddress() {
        val callbacks = object : Callback<AddressResponse> {
            override fun onResponse(call: Call<AddressResponse>?, response: Response<AddressResponse>?) {
                response?.body()?.let {
                    view.updateAddressView(it.data)
                } ?: kotlin.run {
                    view.updateAddressView(arrayListOf())
                }
            }

            override fun onFailure(call: Call<AddressResponse>?, t: Throwable?) {

            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestAddress(callbacks, AddressRequest("th", token, AddressRequestQ(citizen_id)), bearer)
    }

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

            }
        }
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestCitizenInfo(callbacks, CitizenInfoRequest(citizen_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }
}