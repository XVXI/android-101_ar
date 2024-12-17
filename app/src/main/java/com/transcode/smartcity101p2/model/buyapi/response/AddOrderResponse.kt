package com.transcode.smartcity101p2.model.buyapi.response

class AddOrderResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<AddOrderResponseData>()

    class AddOrderResponseData {
        var discount_price: String? = null
        var order_id: String? = null
        var citizen_id: String? = null
        var geo_location: GeoLocation? = null
        var delivery_name: String? = null
        var delivery_address: String? = null
        var delivery_tel: String? = null
        var order_code: String? = null
        var shop_id: String? = null
        var product_price: String? = null
        var order_status: String? = null
        var delivery_price: String? = null
        var total_price: String? = null
    }

    class GeoLocation {
        var type: String? = null
        var coordinates = arrayListOf<String>()
    }
}