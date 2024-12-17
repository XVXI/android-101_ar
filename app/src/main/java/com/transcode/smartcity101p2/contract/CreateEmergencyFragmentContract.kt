package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.emergency.EmergencyTypeResponse

interface CreateEmergencyFragmentContract {
    interface View {
        fun updateTypeList(list: ArrayList<EmergencyTypeResponse.EmergencyTypeResponseData>)
        fun updateCreateEmergency(code: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun getEmergencyType()
        fun createEmergency(emer_detail: String, emer_by: String, emer_lat: String, emer_lng: String, emer_status_id: String, emer_type_id: String, city_id: String, emer_tel: String, file_extension: String, remark: String, img: ArrayList<String>)
    }
}