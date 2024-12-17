package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.EmergencyFragmentContract
import com.transcode.smartcity101p2.model.emergency.EmergencyListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmergencyFragmentPresenter(var view: EmergencyFragmentContract.View) : EmergencyFragmentContract.Presenter {
    override fun getEmergencyList(citizen_id: String) {
        val callbacks = object : Callback<EmergencyListResponse> {
            override fun onResponse(call: Call<EmergencyListResponse>?, response: Response<EmergencyListResponse>?) {
                response?.body()?.let {
                    if(it.code == "200" || it.code == "1"){
                        view.updateEmergencyList(it.data)
                    }
                } ?: kotlin.run {

                }
            }

            override fun onFailure(call: Call<EmergencyListResponse>?, t: Throwable?) {

            }
        }
        ApiRequest.INSTANCE.requestEmergencyList(callbacks, citizen_id)
    }
}