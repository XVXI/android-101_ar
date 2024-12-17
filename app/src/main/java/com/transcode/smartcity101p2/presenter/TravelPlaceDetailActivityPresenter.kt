package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlaceDetailActivityContract
import com.transcode.smartcity101p2.model.AddPlaceCommentResponse
import com.transcode.smartcity101p2.model.AddStampResponse
import com.transcode.smartcity101p2.model.MyStampResponse
import com.transcode.smartcity101p2.model.PlaceDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPlaceDetailActivityPresenter(var view: TravelPlaceDetailActivityContract.View) : TravelPlaceDetailActivityContract.Presenter {
    override fun addComment(citizen_id: String, place_id: String, comment: String, rating: String) {
        val callbacks = object : Callback<AddPlaceCommentResponse> {
            override fun onResponse(call: Call<AddPlaceCommentResponse>?, response: Response<AddPlaceCommentResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateComment(true)
                    } else {
                        view.updateComment(false)
                    }
                } ?: kotlin.run {
                    view.updateComment(false)
                }
            }

            override fun onFailure(call: Call<AddPlaceCommentResponse>?, t: Throwable?) {
//                Log.e("eer", "eer")
                view.updateComment(false)
            }
        }

        ApiRequest.INSTANCE.requestAddPlaceComment(callbacks, citizen_id, place_id, comment, rating)
    }

    override fun getPlaceByID(place_id: String) {
        val callbacks = object : Callback<PlaceDetailsResponse> {
            override fun onResponse(call: Call<PlaceDetailsResponse>?, response: Response<PlaceDetailsResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        it.data?.apply {
                            view.updateView(this)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<PlaceDetailsResponse>?, t: Throwable?) {
//                Log.e("eer", "eer")
            }
        }

        ApiRequest.INSTANCE.requestPlaceByID(callbacks, place_id)
    }

    override fun getMyStamp(citizen_id: String, place_id: String) {
        val callbacks = object : Callback<MyStampResponse> {
            override fun onResponse(call: Call<MyStampResponse>?, response: Response<MyStampResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateMyStamp(it.data, place_id)
                    } else {
                        view.updateMyStamp(arrayListOf(), place_id)
                    }
                } ?: kotlin.run {
                    view.updateMyStamp(arrayListOf(), place_id)
                }
            }

            override fun onFailure(call: Call<MyStampResponse>?, t: Throwable?) {
                view.updateMyStamp(arrayListOf(), place_id)
            }
        }

        ApiRequest.INSTANCE.requestGetMyStamp(callbacks, citizen_id)
    }

    override fun addCheckin(place_id: String, citizen_id: String) {
        val callbacks = object : Callback<AddStampResponse> {
            override fun onResponse(call: Call<AddStampResponse>?, response: Response<AddStampResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.updateCheckin(place_id)
                    } else {
                        view.updateCheckin(place_id)
                    }
                } ?: kotlin.run {
                    view.updateCheckin(place_id)
                }
            }

            override fun onFailure(call: Call<AddStampResponse>?, t: Throwable?) {
                view.updateCheckin(place_id)
            }
        }

        ApiRequest.INSTANCE.addCheckinPlace(callbacks, place_id, citizen_id)
    }
}