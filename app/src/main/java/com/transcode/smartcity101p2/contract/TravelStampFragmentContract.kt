package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyStampResponse
import com.transcode.smartcity101p2.model.ProvinceResponse
import com.transcode.smartcity101p2.model.StampResponse

interface TravelStampFragmentContract {
    interface View {
        fun updateMyStamp(list: ArrayList<MyStampResponse.MyStampResponseData>, place_id: String)
        fun updateStamp(list: ArrayList<StampResponse.StampResponseData>)
        fun updateProvince(list: ArrayList<ProvinceResponse.ProvinceResponseData>)
    }

    interface Presenter {
        fun getMyStamp(citizen_id: String, place_id: String)
        fun getStampByPlaceID(citizen_id: String, place_id: String)
        fun getProvince()
    }
}