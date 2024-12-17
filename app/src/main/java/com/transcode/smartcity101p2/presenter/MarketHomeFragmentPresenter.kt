package com.transcode.smartcity101p2.presenter

import android.util.Log
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketHomeFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.request.*
import com.transcode.smartcity101p2.model.buyapi.response.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketHomeFragmentPresenter(var view: MarketHomeFragmentContract.View) : MarketHomeFragmentContract.Presenter {
    override fun getShopDetail(shop_id: String) {
        val callbacks = object : Callback<GetShopResponse> {
            override fun onResponse(call: Call<GetShopResponse>?, response: Response<GetShopResponse>?) {
                response?.body()?.data?.let {
                    view.getShopSuccess(it)
                } ?: kotlin.run {

                }
            }

            override fun onFailure(call: Call<GetShopResponse>?, t: Throwable?) {
                Log.e("err", "err")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestShopDetail(callbacks, GetShopRequest("th", token, GetShopQ(shop_id)), bearer)
    }

    override fun getAllShop() {
        val callbacks = object : Callback<ShopListResponse> {
            override fun onResponse(call: Call<ShopListResponse>?, response: Response<ShopListResponse>?) {
                response?.body()?.data?.let {
                    view.updateShopList(it)
                } ?: kotlin.run {
                    //                    view.showError("")
                }
            }

            override fun onFailure(call: Call<ShopListResponse>?, t: Throwable?) {
//                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestShopList(callbacks, ShopListRequest("th", token), bearer)
    }

    override fun getShopByKey(key: String, cate: String) {
        val callbacks = object : Callback<ShopListResponse> {
            override fun onResponse(call: Call<ShopListResponse>?, response: Response<ShopListResponse>?) {
                response?.body()?.data?.let {
                    if (it.size > 0) {
                        view.updateShopList(it)
                    } else {
                        onFailure(null, null)
                    }
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<ShopListResponse>?, t: Throwable?) {
                view.updateShopList(arrayListOf())
                view.getShopError()
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestShopByKeyWord(callbacks, SearchShopByKeywordRequest("th", token, SearchShopByKeywordQ(key, cate)), bearer)
    }

    override fun getShopProducts(shop: GetShopResponse.GetShopResponseData, shop_id: String) {
        val callbacks = object : Callback<GetShopProductsResponse> {
            override fun onResponse(call: Call<GetShopProductsResponse>?, response: Response<GetShopProductsResponse>?) {
                response?.body()?.data?.let {
                    view.updateShopProduct(shop, it)
                } ?: kotlin.run {
                    view.updateShopProduct(shop, arrayListOf())
                }
            }

            override fun onFailure(call: Call<GetShopProductsResponse>?, t: Throwable?) {
                view.updateShopProduct(shop, arrayListOf())
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestShopProducts(callbacks, GetShopProductsRequest("th", token, GetShopProductsQ(shop_id)), bearer)
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

    override fun getBestSeller() {
        val callbacks = object : Callback<GetWeeklyBestSellerResponse> {
            override fun onResponse(call: Call<GetWeeklyBestSellerResponse>?, response: Response<GetWeeklyBestSellerResponse>?) {
                response?.body()?.data?.let {
                    view.updateBestSeller(it)
                } ?: kotlin.run {
                    //                    view.showError("")
                }
            }

            override fun onFailure(call: Call<GetWeeklyBestSellerResponse>?, t: Throwable?) {
//                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestWeeklyBestSellerProduct(callbacks, GetWeeklyBestSellerRequest("th", token), bearer)
    }

    override fun getPopular() {
        val callbacks = object : Callback<GetWeeklyPopularResponse> {
            override fun onResponse(call: Call<GetWeeklyPopularResponse>?, response: Response<GetWeeklyPopularResponse>?) {
                response?.body()?.data?.let {
                    view.updatePopular(it)
                } ?: kotlin.run {
                    //                    view.showError("")
                }
            }

            override fun onFailure(call: Call<GetWeeklyPopularResponse>?, t: Throwable?) {
//                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestWeeklyPopularProduct(callbacks, GetWeeklyPopularRequest("th", token), bearer)
    }

    override fun getNewProduct() {
        val callbacks = object : Callback<GetNewProductResponse> {
            override fun onResponse(call: Call<GetNewProductResponse>?, response: Response<GetNewProductResponse>?) {
                response?.body()?.data?.let {
                    view.updateNewProduct(it)
                } ?: kotlin.run {
                    //                    view.showError("")
                }
            }

            override fun onFailure(call: Call<GetNewProductResponse>?, t: Throwable?) {
//                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestNewProduct(callbacks, GetNewProductRequest("th", token, GetNewProductQ("")), bearer)
    }

    override fun getCate() {
        val callbacks = object : Callback<CateResponse> {
            override fun onResponse(call: Call<CateResponse>?, response: Response<CateResponse>?) {
                response?.body()?.let {
                    view.getCateSuccess(it.data)
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<CateResponse>?, t: Throwable?) {
                view.getCateSuccess(arrayListOf())
            }
        }
        ApiRequest.INSTANCE.requestGetCategory(callbacks)
    }

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
}