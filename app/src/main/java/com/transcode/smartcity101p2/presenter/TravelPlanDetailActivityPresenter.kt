package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlanDetailActivityContract
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.PlanDetailResponse
import com.transcode.smartcity101p2.model.travel.request.AddFavPlanRequest
import com.transcode.smartcity101p2.model.travel.request.DeleteFavPlanRequest
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPlanDetailActivityPresenter(var view: TravelPlanDetailActivityContract.View) : TravelPlanDetailActivityContract.Presenter {
    override fun addFavPlan(user_id: String, plan_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                view.updateAddDelete()
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                view.updateAddDelete()
            }
        }

        ApiRequest.INSTANCE.requestAddFavPlan(callbacks, AddFavPlanRequest(plan_id.toInt(), user_id.toInt()))
    }

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

    override fun getPlanDetail(plan_id: String) {
        val callbacks = object : Callback<PlanDetailResponse> {
            override fun onResponse(call: Call<PlanDetailResponse>?, response: Response<PlanDetailResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updatePlanDetail(it.data)
                    } else {
                        view.updatePlanDetail(arrayListOf())
                    }
                } ?: kotlin.run {
                    view.updatePlanDetail(arrayListOf())
                }
            }

            override fun onFailure(call: Call<PlanDetailResponse>?, t: Throwable?) {
                view.updatePlanDetail(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestPlanDetail(callbacks, plan_id)
    }
}