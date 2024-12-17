package com.transcode.smartcity101p2.presenter

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.Profile
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.RegisterFragmentContract
import com.transcode.smartcity101p2.extension.md5
import com.transcode.smartcity101p2.model.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragmentPresenter(var view: RegisterFragmentContract.View) : RegisterFragmentContract.Presenter {

    override fun getFacebookDetail() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
            // Application code
            val emailString = json.getString("email")
            val profile = Profile.getCurrentProfile()
            view.updateFBRegister(profile.firstName, profile.lastName, emailString)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,gender,birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    fun checkRegisterState(isFb: Boolean, name: String, lastname: String, email: String,
                           select_place: Any?, password: String, repassword: String,
                           term_and_con_checkbox: Boolean) {
        if (isFb) {

        } else {
            val notfill = name.isEmpty() || lastname.isEmpty() || email.isEmpty() || (select_place == null) || password.isEmpty() || repassword.isEmpty() || !term_and_con_checkbox
            val checkpassword = (!password.isEmpty() && !repassword.isEmpty()) && (password == repassword)
            if (notfill) {
                view.showError("โปรดกรอกข้อมูล")
            } else {
                if (checkpassword) {
                    registerUser(email, password, email, select_place as CityResponse, name, lastname)
                } else {
                    view.showError("รหัสผ่านไม่ตรงกัน")
                }
            }
        }
    }

    private fun registerUser(username: String, password: String, email: String, select_place: CityResponse, fname: String, lname: String) {
        val callbacks = object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>?, response: Response<RegisterResponse>?) {
                response?.let {
                    if (it.errorBody() != null) {
                        val json = JSONObject(it.errorBody()?.string())
                        view.showError(json.get("message").toString())
                    } else {
                        //success do something
                        if (it.body()?.code == "1" || it.body()?.code == "200") {
//                            requestLogin(username, password, select_place.Cityid ?: "1")
                            view.registerSuccess()
                        } else {
                            view.showError(it.body()?.message.toString())
                        }
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val city_id: String = select_place.Cityid.toString()
        val request = RegisterRequest(username, password.md5(), email, null, city_id, "1", null, null, fname, lname)

        ApiRequest.INSTANCE.requestRegister(callbacks, request)
    }

    private fun requestLogin(user: String, password: String, city_id: String) {
        val callbacks = object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.let {
                    val res = it.body()
                    if (res?.code == "1" || res?.code == "200") {
                        //login success
                        view.onLoginSuccess(res)
                    } else {
                        view.onLoginFail(res?.message.toString())
                    }
                } ?: kotlin.run {
                    view.onLoginFail(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                view.onLoginFail(Const.MESSAGE_ERROR)
            }
        }
        val firebase_client_id = Hawk.get<String>("FCM_TOKEN")
        ApiRequest.INSTANCE.requestLogin(callbacks, user, password.md5(), city_id, firebase_client_id)

    }

}