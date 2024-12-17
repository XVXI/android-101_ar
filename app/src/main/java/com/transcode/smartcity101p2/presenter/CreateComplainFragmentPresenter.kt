package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.CreateComplainFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.complain.ComplainTypeResponse
import com.transcode.smartcity101p2.model.complain.CreateComplainRequest
import com.transcode.smartcity101p2.model.complain.CreateComplainResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateComplainFragmentPresenter(var view: CreateComplainFragmentContract.View) : CreateComplainFragmentContract.Presenter {
    override fun getComplainType() {
        val callbacks = object : Callback<ComplainTypeResponse> {
            override fun onResponse(call: Call<ComplainTypeResponse>?, response: Response<ComplainTypeResponse>?) {
                response?.body()?.let {
                    view.updateTypeList(it.data)
                }
            }

            override fun onFailure(call: Call<ComplainTypeResponse>?, t: Throwable?) {
            }
        }
        ApiRequest.INSTANCE.requestComplainType(callbacks)
    }

    override fun createComplain(complain_detail: String, complain_by: String, complain_lat: String, complain_lng: String, complain_status_id: String, complain_type_id: String, city_id: String, complain_tel: String, file_extension: String, remark: String, img: ArrayList<String>) {
        val callbacks = object : Callback<CreateComplainResponse> {
            override fun onResponse(call: Call<CreateComplainResponse>?, response: Response<CreateComplainResponse>?) {
                response?.body()?.let {
                    view.updateCreateComplain(it.code.toString())
                } ?: kotlin.run {
                    if (response?.errorBody() != null) {
                        try {
                            val json = JSONObject(response?.errorBody()?.string())
                            view.showError(json.get("message").toString())
                        } catch (e: Exception) {
                            view.showError(response?.errorBody()?.string().toString())
                        }
                    } else {
                        view.showError("ร้องเรียนไม่สำเร็จ")
                    }
                }
            }

            override fun onFailure(call: Call<CreateComplainResponse>?, t: Throwable?) {
                view.showError("ร้องเรียนไม่สำเร็จ")
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

        val request = CreateComplainRequest(citizen_id, complain_detail, complain_by, complain_lat, complain_lng, complain_status_id, complain_type_id, city_id, complain_tel, file_extension, remark, arraylimage)
        ApiRequest.INSTANCE.requestCreateComplain(callbacks, request)
    }
}