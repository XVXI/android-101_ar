package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.EmergencyDetailFragmentContract
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.emergency.CreateEmergencyDialogRequest
import com.transcode.smartcity101p2.model.emergency.CreateEmergencyDialogResponse
import com.transcode.smartcity101p2.model.emergency.EmergencyByIDResponse
import com.transcode.smartcity101p2.model.notification.UpdateEmerDialogRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmergencyDetailFragmentPresenter(var view: EmergencyDetailFragmentContract.View) : EmergencyDetailFragmentContract.Presenter {
    override fun getDetailByID(emer_id: String) {
        val callbacks = object : Callback<EmergencyByIDResponse> {
            override fun onResponse(call: Call<EmergencyByIDResponse>?, response: Response<EmergencyByIDResponse>?) {
                response?.body()?.let {
                    view.updateView(it.data)
                }
            }

            override fun onFailure(call: Call<EmergencyByIDResponse>?, t: Throwable?) {
            }
        }
        ApiRequest.INSTANCE.requestEmergencyByID(callbacks, emer_id)
    }

    override fun createDialog(emer_id: String, message: String) {
        val callbacks = object : Callback<CreateEmergencyDialogResponse> {
            override fun onResponse(call: Call<CreateEmergencyDialogResponse>?, response: Response<CreateEmergencyDialogResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.createDialogSuccess()
                    }
                }
            }

            override fun onFailure(call: Call<CreateEmergencyDialogResponse>?, t: Throwable?) {
            }
        }
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = login.authority_info?.CitizenId ?: ""
        val request = CreateEmergencyDialogRequest(emer_id, message, citizen_id, "")
        ApiRequest.INSTANCE.requestCreateEmergencyDialog(callbacks, request)
    }

    override fun updateEmerDialog(emer_id: String, emer_dialog_id: ArrayList<String>) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {

            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
            }
        }
        ApiRequest.INSTANCE.requestUpdateEmerdialog(callbacks, UpdateEmerDialogRequest(emer_id, emer_dialog_id))
    }
}