package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.CreateEmergencyFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.emergency.CreateEmergencyRequest
import com.transcode.smartcity101p2.model.emergency.CreateEmergencyResponse
import com.transcode.smartcity101p2.model.emergency.EmergencyTypeResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateEmergencyFragmentPresenter(var view: CreateEmergencyFragmentContract.View) : CreateEmergencyFragmentContract.Presenter {
    override fun getEmergencyType() {
        val callbacks = object : Callback<EmergencyTypeResponse> {
            override fun onResponse(call: Call<EmergencyTypeResponse>?, response: Response<EmergencyTypeResponse>?) {
                response?.body()?.let {
                    view.updateTypeList(it.data)
                }
            }

            override fun onFailure(call: Call<EmergencyTypeResponse>?, t: Throwable?) {
            }
        }
        ApiRequest.INSTANCE.requestEmergencyType(callbacks)
    }

    override fun createEmergency(emer_detail: String, emer_by: String, emer_lat: String, emer_lng: String, emer_status_id: String, emer_type_id: String, city_id: String, emer_tel: String, file_extension: String, remark: String, img: ArrayList<String>) {
        val callbacks = object : Callback<CreateEmergencyResponse> {
            override fun onResponse(call: Call<CreateEmergencyResponse>?, response: Response<CreateEmergencyResponse>?) {
                response?.body()?.let {
                    view.updateCreateEmergency(it.code.toString())
                } ?: kotlin.run {
                    if (response?.errorBody() != null) {
                        try {
                            val json = JSONObject(response?.errorBody()?.string())
                            view.showError(json.get("message").toString())
                        } catch (e: Exception) {
                            view.showError(response?.errorBody()?.string().toString())
                        }
                    } else {
                        view.showError("แจ้งเหตุไม่สำเร็จ")
                    }
                }
            }

            override fun onFailure(call: Call<CreateEmergencyResponse>?, t: Throwable?) {
                view.showError("แจ้งเหตุไม่สำเร็จ")
            }
        }
        val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = account?.authority_info?.CitizenId ?: ""
        val arraylimage = if (img.size > 0) {
            arrayOfNulls<String>(img.size)
        } else {
            arrayOfNulls(0)
        }
        if (img.size > 0) {
            for (i in 0 until img.size) {
                arraylimage[i] = img[i]
            }
        }
        val request = CreateEmergencyRequest(citizen_id, emer_detail, emer_by, emer_lat, emer_lng, emer_status_id, emer_type_id, city_id, emer_tel, file_extension, remark, arraylimage)
        ApiRequest.INSTANCE.requestCreateEmergency(callbacks, request)
    }
}