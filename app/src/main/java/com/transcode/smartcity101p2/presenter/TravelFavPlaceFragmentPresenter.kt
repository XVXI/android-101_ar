package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelFavPlaceFragmentContract
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.travel.request.DeleteFavPlaceRequest
import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelFavPlaceFragmentPresenter(var view : TravelFavPlaceFragmentContract.View):TravelFavPlaceFragmentContract.Presenter {

    override fun deleteFavPlace(user_id: String, place_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                view.updateAddDelete()
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                view.updateAddDelete()
            }
        }

        ApiRequest.INSTANCE.requestDeleteFavPlace(callbacks, user_id.toInt(), DeleteFavPlaceRequest(place_id.toInt()))
    }

    override fun getFavPlaceList(user_id: String) {
        val callbacks = object : Callback<FavPlaceListResponse> {
            override fun onResponse(call: Call<FavPlaceListResponse>?, response: Response<FavPlaceListResponse>?) {
                response?.body()?.data?.let {
                    view.updateFavPlace(it)
                } ?: kotlin.run {
                    view.updateFavPlace(arrayListOf())
                }
            }

            override fun onFailure(call: Call<FavPlaceListResponse>?, t: Throwable?) {
                view.updateFavPlace(arrayListOf())
            }
        }

        ApiRequest.INSTANCE.requestFavPlaceList(callbacks, user_id)
    }
}