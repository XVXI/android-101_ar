package com.transcode.smartcity101p2.model.buyapi.response

class GetAllOrderResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<GetAllOrderResponseData>()

    class GetAllOrderResponseData {
        var order_id: String? = null
        var order_code: String? = null
        var citizen_id: String? = null
        var product_price: String? = null
        var delivery_price: String? = null
        var discount_price: String? = null
        var total_price: String? = null
        var order_status: String? = null
        var geo_location: GeoLocation? = null
        var delivery_name: String? = null
        var delivery_address: String? = null
        var delivery_tel: String? = null
        var comment: String? = null
        var rating: String? = null
        var address_id: String? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
        var shop_id: String? = null
        var OrderDetails = arrayListOf<OrderDetail>()
        var Shop: Shop? = null
    }

    class OrderDetail {
        var order_id: String? = null
        var product_id: String? = null
        var quantity: String? = null
        var unit_price: String? = null
        var Product: Product? = null
    }

    class Product {
        var product_name: String? = null
        var product_img_url_1: String? = null
        var product_img_url_2: String? = null
        var product_img_url_3: String? = null
        var product_img_url_4: String? = null
        var is_delivery: String? = null
        var price: String? = null
    }

    class GeoLocation {
        var type: String? = null
        var coordinates = arrayListOf<String>()
    }

    class Shop {
        var shop_name: String? = null
        var geo_location: GeoLocation? = null
    }
}