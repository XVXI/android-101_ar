package com.transcode.smartcity101p2.model

class AmphueResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<AmphueResponseData>()

    class AmphueResponseData {
        var AmphueId: String? = null
        var AmphueCode: String? = null
        var AmphueName: String? = null
        var AmphueNameEnd: String? = null
        var GeoId: String? = null
        var ProvinceId: String? = null
    }
}