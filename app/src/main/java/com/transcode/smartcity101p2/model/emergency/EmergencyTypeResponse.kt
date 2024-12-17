package com.transcode.smartcity101p2.model.emergency

class EmergencyTypeResponse {
    var code: String? = null
    var data = arrayListOf<EmergencyTypeResponseData>()

    class EmergencyTypeResponseData {
        var emer_type_id: String? = null
        var emer_type_name: String? = null
    }
}