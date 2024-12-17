package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelFavPlanFragmentContract
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.travel.request.DeleteFavPlanRequest
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelFavPlanFragmentPresenter(var view : TravelFavPlanFragmentContract.View):TravelFavPlanFragmentContract.Presenter {

    override fun deleteFavPlan(user_id: String, plan_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                view.updateAddDelete()
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                view.updateAddDelete()
            }
        }

        ApiRequest.INSTANCE.requestDeleteFavPlan(callbacks, user_id.toInt(), DeleteFavPlanRequest(plan_id.toInt()))
    }

    override fun getFavPlanList(user_id: String) {
        val callbacks = object : Callback<FavPlanListResponse> {
            override fun onResponse(call: Call<FavPlanListResponse>?, response: Response<FavPlanListResponse>?) {
                response?.body()?.data?.let {
                    view.updateFavPlan(it)
                } ?: kotlin.run {
                    view.updateFavPlan(arrayListOf())
                }
            }

            override fun onFailure(call: Call<FavPlanListResponse>?, t: Throwable?) {
                view.updateFavPlan(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestFavPlanList(callbacks, user_id)
    }
}