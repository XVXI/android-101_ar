package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.PlanDetailResponse
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse

interface TravelPlanDetailActivityContract {
    interface View {
        fun updatePlanDetail(data: ArrayList<PlanDetailResponse.PlanDetailResponseData>)
        fun updateFavPlan(data: ArrayList<FavPlanListResponse.FavPlanListResponseData>)
        fun updateAddDelete()
    }

    interface Presenter {
        fun getPlanDetail(plan_id: String)
        fun getFavPlanList(user_id: String)
        fun addFavPlan(user_id: String, plan_id: String)
        fun deleteFavPlan(user_id: String, plan_id: String)
    }
}