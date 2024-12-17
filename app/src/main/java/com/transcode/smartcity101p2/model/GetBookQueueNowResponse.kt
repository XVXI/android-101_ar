package com.transcode.smartcity101p2.model

class GetBookQueueNowResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<MyQueueResponse>()
}