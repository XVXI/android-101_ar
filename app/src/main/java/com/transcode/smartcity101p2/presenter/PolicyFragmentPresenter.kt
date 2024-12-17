package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.PolicyFragmentContract
import com.transcode.smartcity101p2.model.PolicyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PolicyFragmentPresenter(var view: PolicyFragmentContract.View) : PolicyFragmentContract.Presenter {
    override fun getPolicy() {
        val callbacks = object : Callback<PolicyResponse> {
            override fun onResponse(call: Call<PolicyResponse>?, response: Response<PolicyResponse>?) {
                response?.body()?.let {
                    view.updateView(it)
                }
            }

            override fun onFailure(call: Call<PolicyResponse>?, t: Throwable?) {
//                Log.e("PolicyResponse" , "PolicyResponse")
            }
        }

        ApiRequest.INSTANCE.requestPolicy(callbacks)
    }
}