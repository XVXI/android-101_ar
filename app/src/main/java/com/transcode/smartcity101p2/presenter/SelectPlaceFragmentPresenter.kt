package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.SelectPlaceFragmentContract
import com.transcode.smartcity101p2.model.AllCityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectPlaceFragmentPresenter(var view: SelectPlaceFragmentContract.View) : SelectPlaceFragmentContract.Presenter {
    override fun getAllCity(currentLat: String?, currentLan: String?) {
        val callbacks = object : Callback<AllCityResponse> {
            override fun onResponse(call: Call<AllCityResponse>?, response: Response<AllCityResponse>?) {
                if (currentLat!=""){
                    //add nearby place to res
                }
                //send res
                response?.body()?.data?.let {
                    view.updateCityList(it)
                }?:kotlin.run {

                }
            }

            override fun onFailure(call: Call<AllCityResponse>?, t: Throwable?) {
//                Log.e("fail",  "fail")
            }
        }
        val page = "0"
        val limit = "20"
        ApiRequest.INSTANCE.requestAllCity(callbacks, "", page , limit)
    }
}