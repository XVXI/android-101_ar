package com.transcode.smartcity101p2.model

class CctvResponse {
    var code: String? = null
    var message: String? = null
    var data: ArrayList<CctvData>? = null

    class CctvData {
        var Cityid: String? = null
        var Cctvname: String? = null
        var Url: String? = null
        var Lat: String? = null
        var Lng: String? = null
    }
}