package com.transcode.smartcity101p2.model

class TambonResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<TambonResponseData>()

    class TambonResponseData {
        var TambonId: String? = null
        var TambonCode: String? = null
        var TambonName: String? = null
        var TambonNameEnd: String? = null
        var GeoId: String? = null
        var ProvinceId: String? = null
        var AmphueId: String? = null
    }
}