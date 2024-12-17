package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketCartFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.request.AddOrderQ
import com.transcode.smartcity101p2.model.buyapi.request.AddOrderQDetail
import com.transcode.smartcity101p2.model.buyapi.request.AddOrderRequest
import com.transcode.smartcity101p2.model.buyapi.response.AddOrderResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketCartFragmentPresenter(var view: MarketCartFragmentContract.View) : MarketCartFragmentContract.Presenter {
    override fun addOrder(geo_location: String, delivery_name: String, delivery_address: String, delivery_tel: String, selectedOrder: ArrayList<GetProductResponse.GetProductResponseData>) {
        val callbacks = object : Callback<AddOrderResponse> {
            override fun onResponse(call: Call<AddOrderResponse>?, response: Response<AddOrderResponse>?) {
                response?.body()?.let {
                    if (it.code == "1" || it.code == "200") {
                        view.addOrderSuccess(selectedOrder)
                    } else {
                        view.addOrderError()
                    }
                } ?: kotlin.run {
                    view.addOrderError()
                }
            }

            override fun onFailure(call: Call<AddOrderResponse>?, t: Throwable?) {
                view.addOrderError()
            }
        }

        val orderlist = arrayListOf<AddOrderQDetail>()
        for (data in selectedOrder) {
            orderlist.add(AddOrderQDetail(data.product_id
                    ?: "", data.count ?: "1"))
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestAddOrder(callbacks, AddOrderRequest("th", token, AddOrderQ(citizen_id, geo_location, delivery_name, delivery_address, delivery_tel, orderlist)), bearer)
    }
}