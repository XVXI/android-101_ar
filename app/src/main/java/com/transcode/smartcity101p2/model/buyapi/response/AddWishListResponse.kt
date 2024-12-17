package com.transcode.smartcity101p2.model.buyapi.response

class AddWishListResponse {
    var code: String? = null
    var message: String? = null
    var data: AddWishListResponseData? = null

    class AddWishListResponseData {
        var wishlist_id: String? = null
        var citizen_id: String? = null
        var product_id: String? = null
    }
}