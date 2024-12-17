package com.transcode.smartcity101p2.model

class CallPhoneResponse {
    var code: String? = null
    var message: String? = null
    var data: ArrayList<CallPhoneData>? = null

    class CallPhoneData {
        var Callid: String? = null
        var Cityid: String? = null
        var Callname: String? = null
        var Callnumber: String? = null
    }
}