package com.transcode.smartcity101p2.model.buyapi.response

class GetShopResponse {
    var code: String? = null
    var message: String? = null
    var data: GetShopResponseData? = null

    class GetShopResponseData {
        var shop_id: String? = null
        var seller_code: String? = null
        var shop_name: String? = null
        var shop_detail: String? = null
        var geo_location: GeoLocation? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
        var total_coupon_gbck: String? = null
        var shop_owner_id: String? = null
        var open_day_1: String? = null
        var open_day_2: String? = null
        var open_day_3: String? = null
        var open_day_4: String? = null
        var open_day_5: String? = null
        var open_day_6: String? = null
        var open_day_7: String? = null
        var open_time: String? = null
        var close_time: String? = null
        var prepare_minute: String? = null
        var delivery_rate: String? = null
        var is_online: String? = null
        var pd_cat_id: String? = null
        var ProductCategory: ProductCategory? = null
        var ShopOwner: ShopOwner? = null
    }

    class GeoLocation {
        var type: String? = null
        var coordinates = arrayListOf<String>()
    }

    class ProductCategory {
        var pd_cat_name: String? = null
    }

    class ShopOwner {
        var shop_owner_id: String? = null
        var title: String? = null
        var fname: String? = null
        var lname: String? = null
        var tel: String? = null
        var id_card: String? = null
        var birth_date: String? = null
        var province_id: String? = null
        var amphue_id: String? = null
        var tambon_id: String? = null
        var address: String? = null
        var zipcode: String? = null
        var username: String? = null
        var password: String? = null
        var email: String? = null
        var token: String? = null
        var is_active: String? = null
        var shop_owner_img: String? = null
        var registration_token: String? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
    }
}