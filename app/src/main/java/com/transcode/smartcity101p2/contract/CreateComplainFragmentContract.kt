package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.complain.ComplainTypeResponse

interface CreateComplainFragmentContract {
    interface View {
        fun updateTypeList(list: ArrayList<ComplainTypeResponse.ComplainTypeResponseData>)
        fun updateCreateComplain(code: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun getComplainType()
        fun createComplain(complain_detail: String, complain_by: String, complain_lat: String, complain_lng: String, complain_status_id: String, complain_type_id: String, city_id: String, complain_tel: String, file_extension: String, remark: String, img: ArrayList<String>)
    }
}