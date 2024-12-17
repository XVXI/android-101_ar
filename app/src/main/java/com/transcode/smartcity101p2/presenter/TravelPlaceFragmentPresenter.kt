package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlaceFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPlaceFragmentPresenter(var view: TravelPlaceFragmentContract.View) : TravelPlaceFragmentContract.Presenter {
    override fun placeSearchByType(type: String, city_id: String) {
        val callbacks = object : Callback<PlaceListByTypeResponse> {
            override fun onResponse(call: Call<PlaceListByTypeResponse>?, response: Response<PlaceListByTypeResponse>?) {
                response?.body()?.let {
                    if (it.code == "1") {
                        view.updateMaker(it.data)
                    } else {
                        view.updateMaker(arrayListOf())
                    }
                } ?: kotlin.run {
                    view.updateMaker(arrayListOf())
                }
            }

            override fun onFailure(call: Call<PlaceListByTypeResponse>?, t: Throwable?) {
                view.updateMaker(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestPlaceListByType(callbacks, type, city_id)
    }

    override fun placeSearchByTypeTextOnly(text: String, city_id: String) {
        val callbacks = object : Callback<PlaceListByTypeResponse> {
            override fun onResponse(call: Call<PlaceListByTypeResponse>?, response: Response<PlaceListByTypeResponse>?) {
                response?.body()?.let {
                    if (it.code == "1") {
                        view.updateText(it.data)
                    } else {
                        view.updateText(arrayListOf())
                    }
                } ?: kotlin.run {
                    view.updateText(arrayListOf())
                }
            }

            override fun onFailure(call: Call<PlaceListByTypeResponse>?, t: Throwable?) {
                view.updateText(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestPlaceListByText(callbacks, text, city_id)
    }

    override fun placeSearchByText(text: String, city_id: String) {
        val callbacks = object : Callback<PlaceListByTypeResponse> {
            override fun onResponse(call: Call<PlaceListByTypeResponse>?, response: Response<PlaceListByTypeResponse>?) {
                response?.body()?.let {
                    if (it.code == "1") {
                        view.updateSearchText(it.data)
                    } else {
                        view.updateSearchText(arrayListOf())
                    }
                } ?: kotlin.run {
                    view.updateSearchText(arrayListOf())
                }
            }

            override fun onFailure(call: Call<PlaceListByTypeResponse>?, t: Throwable?) {
                view.updateSearchText(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestPlaceListByText(callbacks, text, city_id)
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