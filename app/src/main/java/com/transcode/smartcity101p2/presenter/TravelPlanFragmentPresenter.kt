package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlanFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPlanFragmentPresenter(var view: TravelPlanFragmentContract.View) : TravelPlanFragmentContract.Presenter {
    override fun getPlanList(day: String, city_id: String) {
        val callbacks = object : Callback<PlanListResponse> {
            override fun onResponse(call: Call<PlanListResponse>?, response: Response<PlanListResponse>?) {
                response?.body()?.let {
                    if (it.code == "1") {
                        view.updatePlanList(it.data)
                    } else {
                        view.updatePlanList(arrayListOf())
                    }
                } ?: kotlin.run {
                    view.updatePlanList(arrayListOf())
                }
            }

            override fun onFailure(call: Call<PlanListResponse>?, t: Throwable?) {
                view.updatePlanList(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestPlanList(callbacks, day, city_id)
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