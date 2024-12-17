package com.transcode.smartcity101p2.model.buyapi.response

class AddressResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<AddressResponseData>()

    class AddressResponseData {
        var address_id: String? = null
        var user_id: String? = null
        var name: String? = null
        var address: String? = null
        var tel: String? = null
        var geo_location: String? = null
        var create_datetime: String? = null
    }
}