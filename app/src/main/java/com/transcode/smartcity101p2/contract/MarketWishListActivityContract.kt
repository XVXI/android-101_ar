package com.transcode.smartcity101p2.contract

interface MarketWishListActivityContract {
    interface View {}
    interface Presenter {
        fun addToWishList(product_id: String)
        fun getProductDetail(product_id: String)
    }
}