package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.AmphueResponse
import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.ProvinceResponse
import com.transcode.smartcity101p2.model.TambonResponse

class EditAccountFragmentContract {
    interface View {
        fun updateProvince(list: ArrayList<ProvinceResponse.ProvinceResponseData>)
        fun updateAmphue(list: ArrayList<AmphueResponse.AmphueResponseData>)
        fun updateTambon(list: ArrayList<TambonResponse.TambonResponseData>)
        fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData)
        fun showError(message: String)
        fun editAccountInfoSuccess()
        fun editAccountImageSuccess()
    }

    interface Presenter {
        fun getProvince()
        fun getAmphue(province_id: String)
        fun getTambon(province_id: String, amphue_id: String)
        fun getCitizenInfo()
        fun editAccountImage(email: String, token: String, city_id: String, citizen_id: String, imageData: String?)
        fun editAccountInfo(id_card: String, citizen_id: String, fname: String, lname: String,
                            tel: String, province_id: String, amphue_id: String,
                            tambon_id: String, address: String, zipcode: String, token: String)
    }
}