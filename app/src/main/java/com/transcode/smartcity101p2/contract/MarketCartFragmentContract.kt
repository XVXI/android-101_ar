package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse

interface MarketCartFragmentContract {
    interface View {
        fun addOrderSuccess(selectedOrder: ArrayList<GetProductResponse.GetProductResponseData>)
        fun addOrderError()
    }

    interface Presenter {
        fun addOrder(geo_location: String, delivery_name: String, delivery_address: String, delivery_tel: String, selectedOrder: ArrayList<GetProductResponse.GetProductResponseData>)
    }
}