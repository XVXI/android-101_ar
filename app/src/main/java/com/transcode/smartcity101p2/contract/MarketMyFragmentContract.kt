package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse

interface MarketMyFragmentContract {
    interface View {
        fun updateAddressView(list: ArrayList<AddressResponse.AddressResponseData>)
        fun updateSuccess()
        fun updateError()
        fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData)
    }

    interface Presenter {
        fun getAllAddress()
        fun getCitizenInfo()
    }
}