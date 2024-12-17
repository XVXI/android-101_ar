package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketAddressDetailActivityContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.request.*
import com.transcode.smartcity101p2.model.buyapi.response.AddAddressResponse
import com.transcode.smartcity101p2.model.buyapi.response.RemoveAddressResponse
import com.transcode.smartcity101p2.model.buyapi.response.UpdateAddressResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketAddressDetailActivityPresenter(var view: MarketAddressDetailActivityContract.View) : MarketAddressDetailActivityContract.Presenter {
    override fun deleteAddress(address_id: String) {
        val callbacks = object : Callback<RemoveAddressResponse> {
            override fun onResponse(call: Call<RemoveAddressResponse>?, response: Response<RemoveAddressResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateSuccess()
                    } else {
                        view.updateError()
                    }
                } ?: kotlin.run {
                    view.updateError()
                }
            }

            override fun onFailure(call: Call<RemoveAddressResponse>?, t: Throwable?) {
                view.updateError()
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestRemoveAddress(callbacks, RemoveAddressRequest("th", token, RemoveAddressRequestQ(address_id)), bearer)
    }

    override fun addAddress(name: String, address: String, tel: String) {
        val callbacks = object : Callback<AddAddressResponse> {
            override fun onResponse(call: Call<AddAddressResponse>?, response: Response<AddAddressResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateSuccess()
                    } else {
                        view.updateError()
                    }
                } ?: kotlin.run {
                    view.updateError()
                }
            }

            override fun onFailure(call: Call<AddAddressResponse>?, t: Throwable?) {
                view.updateError()
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestAddAddress(callbacks, AddAddressRequest("th", token, AddAddressRequestQ(citizen_id, name, address, tel)), bearer)
    }

    override fun updateAddress(citizen_address_id: String, name: String, address: String, tel: String) {
        val callbacks = object : Callback<UpdateAddressResponse> {
            override fun onResponse(call: Call<UpdateAddressResponse>?, response: Response<UpdateAddressResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateSuccess()
                    } else {
                        view.updateError()
                    }
                } ?: kotlin.run {
                    view.updateError()
                }
            }

            override fun onFailure(call: Call<UpdateAddressResponse>?, t: Throwable?) {
                view.updateError()
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestUpdateAddress(callbacks, UpdateAddressRequest("th", token, UpdateAddressRequestQ(citizen_id, citizen_address_id, name, address, tel, "0")), bearer)
    }
}