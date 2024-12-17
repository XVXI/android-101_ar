package com.transcode.smartcity101p2.model.buyapi.response

class AddAddressResponse {
    var code: String? = null
    var message: String? = null
    var data: AddAddressResponseData? = null

    class AddAddressResponseData {
        var address_id: String? = null
        var user_id: String? = null
        var name: String? = null
        var address: String? = null
        var tel: String? = null
    }
}