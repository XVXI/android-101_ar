package com.transcode.smartcity101p2.model.buyapi.response

class GetShopProductsResponse {
    var code: String? = null
    var message: String? = null
    var data = ArrayList<GetProductResponse.GetProductResponseData>()
}