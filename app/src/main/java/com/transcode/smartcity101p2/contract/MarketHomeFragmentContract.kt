package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.buyapi.response.CateListResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetShopResponse

interface MarketHomeFragmentContract {
    interface View {
        fun updateShopList(list: ArrayList<GetShopResponse.GetShopResponseData>)
        fun updateShopProduct(shop: GetShopResponse.GetShopResponseData, list: ArrayList<GetProductResponse.GetProductResponseData>)
        fun updateBestSeller(list: ArrayList<GetProductResponse.GetProductResponseData>)
        fun updatePopular(list: ArrayList<GetProductResponse.GetProductResponseData>)
        fun updateNewProduct(list: ArrayList<GetProductResponse.GetProductResponseData>)
        fun getShopSuccess(res: GetShopResponse.GetShopResponseData)
        fun getShopError()
        fun getCateSuccess(list: ArrayList<CateListResponse>)
    }

    interface Presenter {
        fun getAllShop()
        fun getShopByKey(key: String, cate: String)
        fun getShopProducts(shop: GetShopResponse.GetShopResponseData, shop_id: String)
        fun addToWishList(product_id: String)
        fun getBestSeller()
        fun getPopular()
        fun getNewProduct()
        fun getShopDetail(shop_id: String)
        fun getCate()
        fun getProductDetail(product_id: String)
    }
}