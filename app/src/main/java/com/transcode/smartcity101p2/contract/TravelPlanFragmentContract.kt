package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.PlanListResponse
import com.transcode.smartcity101p2.model.ProvinceResponse

interface TravelPlanFragmentContract {
    interface View {
        fun updatePlanList(list: ArrayList<PlanListResponse.PlanListResponseData>)
        fun updateProvince(data: ArrayList<ProvinceResponse.ProvinceResponseData>)
    }

    interface Presenter {
        fun getProvince()
        fun getPlanList(day: String, city_id: String)
    }
}