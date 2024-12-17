package com.transcode.smartcity101p2.model

class MyStampResponse {
    var code: String? = null
    var data = arrayListOf<MyStampResponseData>()

    class MyStampResponseData {
        var place_id: String? = null
        var place_name: String? = null
        var stamp_img: String? = null
    }
}