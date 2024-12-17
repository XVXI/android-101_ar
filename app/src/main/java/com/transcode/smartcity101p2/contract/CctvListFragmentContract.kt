package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CctvResponse

interface CctvListFragmentContract {
    interface View {
        fun updateList(list: ArrayList<CctvResponse.CctvData>)
    }

    interface Presenter {
        fun getCctvList(city_id: String, token: String)
    }
}