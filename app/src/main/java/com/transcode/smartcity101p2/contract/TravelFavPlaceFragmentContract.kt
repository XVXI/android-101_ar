package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse

interface TravelFavPlaceFragmentContract {
    interface View {
        fun updateFavPlace(data: ArrayList<FavPlaceListResponse.FavPlaceListResponseData>)
        fun updateAddDelete()
    }

    interface Presenter {
        fun getFavPlaceList(user_id: String)
        fun deleteFavPlace(user_id: String, place_id: String)
    }
}