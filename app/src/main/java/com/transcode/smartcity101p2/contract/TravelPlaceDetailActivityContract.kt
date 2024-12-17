package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyStampResponse
import com.transcode.smartcity101p2.model.PlaceDetailsResponse

interface TravelPlaceDetailActivityContract {
    interface View {
        fun updateView(data: PlaceDetailsResponse.PlaceDetailsResponseData)
        fun updateComment(success: Boolean)
        fun updateMyStamp(list: ArrayList<MyStampResponse.MyStampResponseData>, place_id: String)
        fun updateCheckin(place_id: String)
    }

    interface Presenter {
        fun getPlaceByID(place_id: String)
        fun addComment(citizen_id: String, place_id: String, comment: String, rating: String)
        fun getMyStamp(citizen_id: String, place_id: String)
        fun addCheckin(place_id: String, citizen_id: String)
    }
}