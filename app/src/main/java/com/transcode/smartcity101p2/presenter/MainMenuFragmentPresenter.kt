package com.transcode.smartcity101p2.presenter

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MainMenuFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainMenuFragmentPresenter(var view: MainMenuFragmentContract.View) : MainMenuFragmentContract.Presenter {
    override fun requestLoginGuest(udid: String) {
        val callbacks = object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.let {
                    val res = it.body()
                    if (res?.code == "1" || res?.code == "200") {
                        //login success
                        Hawk.put("tmp_token", res.authority_info?.getAllToken() ?: "")
                        Hawk.put("tmp_bearer", "Bearer " + (res.server?.token ?: ""))
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
        ApiRequest.INSTANCE.requestLoginGuest(callbacks, udid, "2", firebase_client_id, "", "", "")

    }

    fun requestLoginFacebook(email: String, fname: String, lname: String) {
        val callbacks = object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.let {
                    val res = it.body()
                    if (res?.code == "1" || res?.code == "200") {
                        //login success
                        Hawk.put("tmp_token", res.authority_info?.getAllToken() ?: "")
                        Hawk.put("tmp_bearer", "Bearer " + (res.server?.token ?: ""))
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

        val accessToken = AccessToken.getCurrentAccessToken()
        val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
            // Application code
            val fb_id = try {
                json.getString("id")
            } catch (e: Exception) {
                ""
            }
            val firebase_client_id = Hawk.get<String>("FCM_TOKEN")
            ApiRequest.INSTANCE.requestLoginGuest(callbacks, fb_id, "1", firebase_client_id, email, fname, lname)

        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,gender,birthday")
        request.parameters = parameters
        request.executeAsync()
    }

    fun updateCityAccount(email: String, fname: String, lname: String, udid: String, city_id: String) {
        val callbacks = object : Callback<EditCityAccountResponse> {
            override fun onResponse(call: Call<EditCityAccountResponse>?, response: Response<EditCityAccountResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.onUpdateDataComplete()
                    } else {
                        view.onLoginFail(it.message.toString())
                    }
                } ?: kotlin.run {
                    view.onLoginFail(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<EditCityAccountResponse>?, t: Throwable?) {
                view.onLoginFail(Const.MESSAGE_ERROR)
            }
        }
        val token = Hawk.get<String>("tmp_token")
        val bearer = Hawk.get<String>("tmp_bearer")
        val request = EditCityAccountRequest(email, fname, lname, token, udid, city_id)
        ApiRequest.INSTANCE.requestEditCityAccount(callbacks, request, bearer)
    }

    override fun getCitizenInfo() {
        val callbacks = object : Callback<CitizenInfoResponse> {
            override fun onResponse(call: Call<CitizenInfoResponse>?, response: Response<CitizenInfoResponse>?) {
                response?.body()?.data?.let {
                    it.CitizenId?.apply {
                        view.onGetCitizenInfoSuccess(it)
                    }
                } ?: kotlin.run {
                    LoginManager.getInstance().logOut()
                }
            }

            override fun onFailure(call: Call<CitizenInfoResponse>?, t: Throwable?) {
                LoginManager.getInstance().logOut()
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestCitizenInfo(callbacks, CitizenInfoRequest(citizen_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }

    fun editAccountInfo(id_card: String, citizen_id: String, fname: String, lname: String, tel: String, province_id: String, amphue_id: String, tambon_id: String, address: String, zipcode: String, token: String) {
        val callbacks = object : Callback<EditCitizenInfoResponse> {
            override fun onResponse(call: Call<EditCitizenInfoResponse>?, response: Response<EditCitizenInfoResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.onUpdateDataComplete()
                    } else {
                        view.showError(it.message.toString())
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<EditCitizenInfoResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestEditCitizenInfo(callbacks, EditCitizenInfoRequest(id_card, citizen_id, fname, lname,
                tel, province_id, amphue_id, tambon_id,
                address, zipcode, token)
                , bearer)
    }

    fun requestLoginGoogle(gg_id: String, email: String, fname: String, lname: String) {
        val callbacks = object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                response?.let {
                    val res = it.body()
                    if (res?.code == "1" || res?.code == "200") {
                        //login success
                        Hawk.put("tmp_token", res.authority_info?.getAllToken() ?: "")
                        Hawk.put("tmp_bearer", "Bearer " + (res.server?.token ?: ""))
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
        ApiRequest.INSTANCE.requestLoginGoogle(callbacks, gg_id, "3", firebase_client_id, email, fname, lname)
    }
}