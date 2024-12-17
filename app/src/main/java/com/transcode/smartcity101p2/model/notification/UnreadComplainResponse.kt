package com.transcode.smartcity101p2.model.notification

class UnreadComplainResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<UnreadEmerResponseData>()

    class UnreadEmerResponseData {
        var complain_id: String? = null
        var unread: String? = null
    }
}