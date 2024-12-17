package com.transcode.smartcity101p2.model

class NewsImgResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<ImageData>()

    class ImageData {
        var NewsImgId: String? = null
        var ImgUrl: String? = null
    }
}