package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketOrderActivityContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.request.ConfirmReceiveOrderQ
import com.transcode.smartcity101p2.model.buyapi.request.ConfirmReceiveOrderRequest
import com.transcode.smartcity101p2.model.buyapi.request.GetAllOrderQ
import com.transcode.smartcity101p2.model.buyapi.request.GetAllOrderRequest
import com.transcode.smartcity101p2.model.buyapi.response.ConfirmReceiveOrderResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetAllOrderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketOrderActivityPresenter(var view: MarketOrderActivityContract.View) : MarketOrderActivityContract.Presenter {
    override fun confirmReceiveOrder(order_id: String, score: String, comment: String) {
        val callbacks = object : Callback<ConfirmReceiveOrderResponse> {
            override fun onResponse(call: Call<ConfirmReceiveOrderResponse>?, response: Response<ConfirmReceiveOrderResponse>?) {
                response?.body()?.let {
                    if (it.code == "200" || it.code == "1") {
                        view.confirmReceiveOrderSuccess()
                    } else {
                        view.confirmReceiveOrderFail()
                    }
                } ?: kotlin.run {
                    view.confirmReceiveOrderFail()
                }
            }

            override fun onFailure(call: Call<ConfirmReceiveOrderResponse>?, t: Throwable?) {
                view.confirmReceiveOrderFail()
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestConfirmReceiveOrder(callbacks, ConfirmReceiveOrderRequest("th", token, ConfirmReceiveOrderQ(order_id, score, comment)), bearer)
    }

    override fun getAllOrders() {
        val callbacks = object : Callback<GetAllOrderResponse> {
            override fun onResponse(call: Call<GetAllOrderResponse>?, response: Response<GetAllOrderResponse>?) {
                response?.body()?.let {
                    //                    0 = สั่ง
//                    1 = กำลังส่ง
//                    2 = ส่งเสร็จแล้ว
//                    3 = ลูกค้า confirm ว่าได้สินค้า
//                    4 = ยกเลิก

//                    0; // ลูกค้าสั่ง
//                    1; // ร้านค้า confirm
//                    2; // กำลังส่ง
//                    3; // ส่งเสร็จแล้ว xxx
//                    4; // ลูกค้า confirm ว่าได้สินค้า
//                    5; // ร้านยกเลิก
//                    6; // ลูกค้ายกเลิก
                    val list = arrayListOf<GetAllOrderResponse.GetAllOrderResponseData>()
                    for (data in it.data) {
                        if (data.order_status != "4") {
                            list.add(0, data)
                        }
                    }
                    view.getOrderSuccess(list)
                } ?: kotlin.run {
                    view.getOrderSuccess(arrayListOf())
                }
            }

            override fun onFailure(call: Call<GetAllOrderResponse>?, t: Throwable?) {
                view.getOrderSuccess(arrayListOf())
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
//        ApiRequest.INSTANCE.requestAllOrder(callbacks, GetAllOrderRequest("th", token, GetAllOrderQ("1")), bearer)
        ApiRequest.INSTANCE.requestAllOrder(callbacks, GetAllOrderRequest("th", token, GetAllOrderQ(citizen_id)), bearer)
    }
}