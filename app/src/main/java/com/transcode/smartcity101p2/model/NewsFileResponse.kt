package com.transcode.smartcity101p2.model

class NewsFileResponse {
    var data = arrayListOf<NewsFileResponseData>()

    class NewsFileResponseData {
        var NewsFile: String? = null
        var FileUrl: String? = null
    }
}