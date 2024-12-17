package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.buyapi.response.GetAllOrderResponse

interface MarketOrderActivityContract {
    interface View {
        fun getOrderSuccess(list: ArrayList<GetAllOrderResponse.GetAllOrderResponseData>)
        fun confirmReceiveOrderSuccess()
        fun confirmReceiveOrderFail()
    }

    interface Presenter {
        fun getAllOrders()
        fun confirmReceiveOrder(order_id: String, score: String, comment: String)
    }
}