package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.PlaceListByTypeResponse
import com.transcode.smartcity101p2.model.ProvinceResponse

interface TravelPlaceFragmentContract {
    interface View {
        fun updateMaker(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>)
        fun updateText(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>)
        fun updateSearchText(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>)
        fun updateProvince(data: ArrayList<ProvinceResponse.ProvinceResponseData>)
    }

    interface Presenter {
        fun getProvince()
        fun placeSearchByType(type: String, city_id: String)
        fun placeSearchByTypeTextOnly(type: String, city_id: String)
        fun placeSearchByText(text: String, city_id: String)
    }
}