package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.ComplainDetailFragmentContract
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.complain.ComplainByIDResponse
import com.transcode.smartcity101p2.model.complain.CreateComplainDialogRequest
import com.transcode.smartcity101p2.model.complain.CreateComplainDialogResponse
import com.transcode.smartcity101p2.model.notification.UpdateComplainDialogRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplainDetailFragmentPresenter(var view: ComplainDetailFragmentContract.View) : ComplainDetailFragmentContract.Presenter {
    override fun createDialog(complain_id: String, message: String) {
        val callbacks = object : Callback<CreateComplainDialogResponse> {
            override fun onResponse(call: Call<CreateComplainDialogResponse>?, response: Response<CreateComplainDialogResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.createDialogSuccess()
                    }
                }
            }

            override fun onFailure(call: Call<CreateComplainDialogResponse>?, t: Throwable?) {
            }
        }
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = login.authority_info?.CitizenId ?: ""
        val request = CreateComplainDialogRequest(complain_id, message, citizen_id, "")
        ApiRequest.INSTANCE.requestCreateComplainDialog(callbacks, request)
    }

    override fun getDetailByID(complain_id: String) {
        val callbacks = object : Callback<ComplainByIDResponse> {
            override fun onResponse(call: Call<ComplainByIDResponse>?, response: Response<ComplainByIDResponse>?) {
                response?.body()?.let {
                    view.updateView(it.data)
                }
            }

            override fun onFailure(call: Call<ComplainByIDResponse>?, t: Throwable?) {
            }
        }
        ApiRequest.INSTANCE.requestComplainByID(callbacks, complain_id)
    }

    override fun updateComplainDialog(complain_id: String, complain_dialog_id: ArrayList<String>) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {

            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
            }
        }
        ApiRequest.INSTANCE.requestUpdateComplaindialog(callbacks, UpdateComplainDialogRequest(complain_id, complain_dialog_id))
    }
}