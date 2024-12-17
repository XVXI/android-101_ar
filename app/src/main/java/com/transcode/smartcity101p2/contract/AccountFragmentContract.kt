package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.CityFunctionResponse

interface AccountFragmentContract {
    interface View {
        fun updateList(dataList: ArrayList<CityFunctionResponse>)
        fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData)
    }

    interface Presenter {
        fun getCityFunction()
        fun getCitizenInfo()
    }
}