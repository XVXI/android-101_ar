package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CallPhoneResponse

interface CallPhoneFragmentContract {
    interface View {
        fun updateList(list: ArrayList<CallPhoneResponse.CallPhoneData>)
    }

    interface Presenter {
        fun getCallPhoneNumber(city_id: String, token: String)
    }
}