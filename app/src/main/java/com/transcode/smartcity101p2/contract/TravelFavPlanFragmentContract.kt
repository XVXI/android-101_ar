package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse

interface TravelFavPlanFragmentContract {
    interface View {
        fun updateFavPlan(data: ArrayList<FavPlanListResponse.FavPlanListResponseData>)
        fun updateAddDelete()
    }

    interface Presenter {
        fun getFavPlanList(user_id: String)
        fun deleteFavPlan(user_id: String, plan_id: String)
    }
}