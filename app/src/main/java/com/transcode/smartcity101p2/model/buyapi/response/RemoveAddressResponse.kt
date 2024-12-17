package com.transcode.smartcity101p2.model.buyapi.response

class RemoveAddressResponse {
    var code: String? = null
    var message: String? = null
    var data: RemoveAddressResponseData? = null

    class RemoveAddressResponseData {
        var msg: String? = null
    }
}