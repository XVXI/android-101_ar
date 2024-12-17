package com.transcode.smartcity101p2.model.complain

class ComplainTypeResponse {
    var code: String? = null
    var data = arrayListOf<ComplainTypeResponseData>()

    class ComplainTypeResponseData {
        var complain_type_id: String? = null
        var complain_type_name: String? = null
    }
}