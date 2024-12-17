package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse

interface MarketAddressListActivityContract {
    interface View {
        fun updateAddressView(list: ArrayList<AddressResponse.AddressResponseData>)
        fun updateSuccess()
        fun updateError()
    }

    interface Presenter {
        fun getAllAddress()
    }
}