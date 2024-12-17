package com.transcode.smartcity101p2.presenter

import android.util.Log
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketWishListActivityContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.request.AddWishListQ
import com.transcode.smartcity101p2.model.buyapi.request.AddWishListRequest
import com.transcode.smartcity101p2.model.buyapi.request.GetProductQ
import com.transcode.smartcity101p2.model.buyapi.request.GetProductRequest
import com.transcode.smartcity101p2.model.buyapi.response.AddWishListResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketWishListActivityPresenter(var view: MarketWishListActivityContract.View) : MarketWishListActivityContract.Presenter {
    override fun getProductDetail(product_id: String) {
        val callbacks = object : Callback<GetProductResponse> {
            override fun onResponse(call: Call<GetProductResponse>?, response: Response<GetProductResponse>?) {
                Log.e("success", "success")
            }

            override fun onFailure(call: Call<GetProductResponse>?, t: Throwable?) {
                Log.e("error", "error")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestProductDetail(callbacks, GetProductRequest("th", token, GetProductQ(product_id)), bearer)
    }

    override fun addToWishList(product_id: String) {
        val callbacks = object : Callback<AddWishListResponse> {
            override fun onResponse(call: Call<AddWishListResponse>?, response: Response<AddWishListResponse>?) {
//                response?.body()?.data?.let {
//                    view.updateShopProduct(shop, it)
//                } ?: kotlin.run {
                //                    view.showError("")
//                }
            }

            override fun onFailure(call: Call<AddWishListResponse>?, t: Throwable?) {
//                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestAddWishList(callbacks, AddWishListRequest("th", token, AddWishListQ(citizen_id, product_id)), bearer)
    }
}