package com.transcode.smartcity101p2.model

class StampResponse {
    var code: String? = null
    var stamp_place = arrayListOf<StampResponseData>()


    class StampResponseData {
        var place_id: String? = null
        var place_name: String? = null
        var stamp_img: String? = null
    }
}