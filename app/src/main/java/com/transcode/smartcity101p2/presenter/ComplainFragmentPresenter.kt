package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.ComplainFragmentContract
import com.transcode.smartcity101p2.model.complain.ComplainListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplainFragmentPresenter(var view: ComplainFragmentContract.View) : ComplainFragmentContract.Presenter {
    override fun getComplainList(citizen_id: String) {
        val callbacks = object : Callback<ComplainListResponse> {
            override fun onResponse(call: Call<ComplainListResponse>?, response: Response<ComplainListResponse>?) {
                response?.body()?.let {
                    if(it.code == "200" || it.code == "1"){
                        view.updateComplainList(it.data)
                    }
                } ?: kotlin.run {

                }
            }

            override fun onFailure(call: Call<ComplainListResponse>?, t: Throwable?) {

            }
        }
        ApiRequest.INSTANCE.requestComplainList(callbacks, citizen_id)
    }
}