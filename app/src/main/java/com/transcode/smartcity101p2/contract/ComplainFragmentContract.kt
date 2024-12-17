package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.complain.ComplainListResponse

interface ComplainFragmentContract {
    interface View {
        fun updateComplainList(list: ArrayList<ComplainListResponse.ComplainListResponseData>)
    }

    interface Presenter {
        fun getComplainList(citizen_id: String)
    }
}