package com.transcode.smartcity101p2.model.buyapi.response

class GetProductResponse {
    var code: String? = null
    var message: String? = null
    var data: GetProductResponseData? = null

    class GetProductResponseData {
        var product_id: String? = null
        var product_name: String? = null
        var product_detail: String? = null
        var price: String? = null
        var product_img_url_1: String? = null
        var product_img_url_2: String? = null
        var product_img_url_3: String? = null
        var product_img_url_4: String? = null
        var shop_id: String? = null
        var is_delivery: String? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
        var Shop = Shop()

        var count: String? = null
        var shop_name: String? = null
        var is_selected = false
        var show_delete = false
    }

    class Shop {
        var shop_name: String? = null
        var delivery_rate: String? = null
        var ShopOwner = GetShopResponse.ShopOwner()
    }
}