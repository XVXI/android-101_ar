package com.transcode.smartcity101p2.model

class PlaceTypeResponse {
    var code: String? = null
    var data = arrayListOf<PlaceTypeResponseData>()

    class PlaceTypeResponseData {
        var type_id: String? = null
        var type_name: String? = null
        var icon_url: String? = null
        var city_id: String? = null
    }
}