package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.ForgotFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.ForgotPasswordRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotFragmentPresenter(var view: ForgotFragmentContract.View) : ForgotFragmentContract.Presenter {
    override fun forgotPassword(email: String) {
        if (email.isEmpty()){
            view.showError("โปรดใส่อีเมลให้ถูกต้อง")
        }else{
            val callbacks = object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    view.updateView()
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            ApiRequest.INSTANCE.requestForgotPassword(callbacks, ForgotPasswordRequest(email))
        }
    }
}