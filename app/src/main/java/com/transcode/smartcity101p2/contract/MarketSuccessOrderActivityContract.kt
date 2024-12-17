package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.buyapi.response.GetAllOrderResponse

interface MarketSuccessOrderActivityContract {
    interface View {
        fun getOrderSuccess(list:ArrayList<GetAllOrderResponse.GetAllOrderResponseData>)
    }

    interface Presenter {
        fun getSuccessOrders()
    }
}