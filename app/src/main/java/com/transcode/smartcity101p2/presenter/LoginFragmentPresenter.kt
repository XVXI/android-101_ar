package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.LoginFragmentContract
import com.transcode.smartcity101p2.extension.md5
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragmentPresenter(val view: LoginFragmentContract.View) : LoginFragmentContract.Presenter {
    override fun requestLogin(user: String, password: String, city_id: String) {

        view.showProgressBar()

        val callbacks = object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                view.hideProgressBar()
                response?.let {
                    val res = it.body()
                    val error = it.errorBody()
                    res?.let { d ->
                        if (d.code == "1") {
                            //login success
                            view.onLoginSuccess(res)
                        } else {
                            view.onLoginFail(d.message.toString())
                        }
                    } ?: kotlin.run {
                        try {
                            val json = JSONObject(error?.string())
                            view.onLoginFail(json.getString("message"))
                        } catch (e: Exception) {
                            view.onLoginFail(Const.MESSAGE_ERROR)
                        }
                    }
                } ?: kotlin.run {
                    view.onLoginFail(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                view.hideProgressBar()
                view.onLoginFail(Const.MESSAGE_ERROR)
            }
        }
        val firebase_client_id = Hawk.get<String>("FCM_TOKEN")
        ApiRequest.INSTANCE.requestLogin(callbacks, user, password.md5(), city_id, firebase_client_id)
    }
}