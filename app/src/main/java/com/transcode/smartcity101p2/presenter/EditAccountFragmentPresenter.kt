package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.EditAccountFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAccountFragmentPresenter(var view: EditAccountFragmentContract.View) : EditAccountFragmentContract.Presenter {
    override fun editAccountImage(email: String, token: String, city_id: String, citizen_id: String, imageData: String?) {
        val callbacks = object : Callback<EditcitizenAccountRespons> {
            override fun onResponse(call: Call<EditcitizenAccountRespons>?, response: Response<EditcitizenAccountRespons>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.editAccountImageSuccess()
                    } else {
                        view.showError(it.message.toString())
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<EditcitizenAccountRespons>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val firebase_client_id = Hawk.get<String>("FCM_TOKEN")
        ApiRequest.INSTANCE.requestEditCitizenAccount(callbacks, EditcitizenAccountRequest(email, token, city_id, citizen_id, imageData, firebase_client_id), bearer)
    }

    override fun editAccountInfo(id_card: String, citizen_id: String, fname: String, lname: String, tel: String, province_id: String, amphue_id: String, tambon_id: String, address: String, zipcode: String, token: String) {
        val callbacks = object : Callback<EditCitizenInfoResponse> {
            override fun onResponse(call: Call<EditCitizenInfoResponse>?, response: Response<EditCitizenInfoResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.editAccountInfoSuccess()
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

    override fun getTambon(province_id: String, amphue_id: String) {
        val callbacks = object : Callback<TambonResponse> {
            override fun onResponse(call: Call<TambonResponse>?, response: Response<TambonResponse>?) {
                response?.body()?.let {
                    view.updateTambon(it.data)
                }
            }

            override fun onFailure(call: Call<TambonResponse>?, t: Throwable?) {
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val request = TambonRequest("th", loginResponse.authority_info?.getAllToken()
                ?: "", TambonQRequest(province_id, amphue_id))
        ApiRequest.INSTANCE.requestTambon(callbacks, request, bearer)
    }

    override fun getAmphue(province_id: String) {
        val callbacks = object : Callback<AmphueResponse> {
            override fun onResponse(call: Call<AmphueResponse>?, response: Response<AmphueResponse>?) {
                response?.body()?.let {
                    view.updateAmphue(it.data)
                }
            }

            override fun onFailure(call: Call<AmphueResponse>?, t: Throwable?) {
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val request = AmphueRequest("th", loginResponse.authority_info?.getAllToken()
                ?: "", AmphueQRequest(province_id))
        ApiRequest.INSTANCE.requestAmphue(callbacks, request, bearer)
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

    override fun getCitizenInfo() {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val callbacks = object : Callback<CitizenInfoResponse> {
            override fun onResponse(call: Call<CitizenInfoResponse>?, response: Response<CitizenInfoResponse>?) {
                response?.body()?.data?.let {
                    it.CitizenId?.apply {
//                        if(loginResponse.authority_info?.IsFb == "1"){
//                            it.Email = "FaceBook"
//                        }
                        view.updateCitizenInfo(it)
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<CitizenInfoResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestCitizenInfo(callbacks, CitizenInfoRequest(citizen_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }
}