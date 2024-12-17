package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CityResponse

interface SelectPlaceFragmentContract {
    interface View {
        fun updateCityList(list:ArrayList<CityResponse>)
    }

    interface Presenter {
        fun getAllCity(currentLat: String? = "", currentLan: String? = "")
    }
}