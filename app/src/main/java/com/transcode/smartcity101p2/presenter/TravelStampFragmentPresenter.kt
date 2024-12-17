package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelStampFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelStampFragmentPresenter(var view: TravelStampFragmentContract.View) : TravelStampFragmentContract.Presenter {
    override fun getStampByPlaceID(citizen_id: String, place_id: String) {
        val callbacks = object : Callback<StampResponse> {
            override fun onResponse(call: Call<StampResponse>?, response: Response<StampResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateStamp(it.stamp_place)
                    } else {
                        view.updateStamp(arrayListOf())
                    }
                } ?: kotlin.run {
                    view.updateStamp(arrayListOf())
                }
            }

            override fun onFailure(call: Call<StampResponse>?, t: Throwable?) {
                view.updateStamp(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestGetPlaceStamp(callbacks, place_id, citizen_id)
    }

    override fun getMyStamp(citizen_id: String, place_id: String) {
        val callbacks = object : Callback<MyStampResponse> {
            override fun onResponse(call: Call<MyStampResponse>?, response: Response<MyStampResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateMyStamp(it.data, place_id)
                    } else {
                        view.updateMyStamp(arrayListOf(), place_id)
                    }
                } ?: kotlin.run {
                    view.updateMyStamp(arrayListOf(), place_id)
                }
            }

            override fun onFailure(call: Call<MyStampResponse>?, t: Throwable?) {
                view.updateMyStamp(arrayListOf(), place_id)
            }
        }

        ApiRequest.INSTANCE.requestGetMyStamp(callbacks, citizen_id)
    }

    override fun getProvince() {
        val callbacks = object : Callback<ProvinceResponse> {
            override fun onResponse(call: Call<ProvinceResponse>?, response: Response<ProvinceResponse>?) {
                response?.body()?.let {
                    view.updateProvince(it.data)
                }
            }

            override fun onFailure(call: Call<ProvinceResponse>?, t: Throwable?) {
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val request = ProvinceRequest("th", loginResponse.authority_info?.getAllToken()
                ?: "")
        ApiRequest.INSTANCE.requestProvince(callbacks, request, bearer)
    }
}