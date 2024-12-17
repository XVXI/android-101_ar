package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.emergency.EmergencyListResponse

interface EmergencyFragmentContract {
    interface View {
        fun updateEmergencyList(list: ArrayList<EmergencyListResponse.EmergencyListResponseData>)
    }

    interface Presenter {
        fun getEmergencyList(citizen_id: String)
    }
}