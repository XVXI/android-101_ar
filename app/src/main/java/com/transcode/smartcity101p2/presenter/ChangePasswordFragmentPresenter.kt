package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.ChangePasswordFragmentContract
import com.transcode.smartcity101p2.extension.md5
import com.transcode.smartcity101p2.model.ChangePasswordRequest
import com.transcode.smartcity101p2.model.ChangePasswordResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragmentPresenter(var view: ChangePasswordFragmentContract.View) : ChangePasswordFragmentContract.Presenter {
    override fun changePassword(email: String, password: String, newpassword: String, token: String) {
        val callbacks = object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>?, response: Response<ChangePasswordResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.changePasswordSuccess()
                    } else {
                        view.showError(it.message.toString())
                    }
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestResetPassword(callbacks, ChangePasswordRequest(email, password.md5(), newpassword.md5(), token), bearer)
    }
}