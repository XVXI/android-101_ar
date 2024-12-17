package com.transcode.smartcity101p2.model.emergency

class EmergencyListResponse {
    var code: String? = null
    var data = arrayListOf<EmergencyListResponseData>()

    class EmergencyListResponseData {
        var emer_id: String? = null
        var citizen_id: String? = null
        var emer_detail: String? = null
        var emer_lat: String? = null
        var emer_lng: String? = null
        var emer_type_id: String? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
        var emer_status_id: String? = null
        var rate: String? = null
        var comment: String? = null
        var rate_datetime: String? = null
        var emer_by: String? = null
        var emer_tel: String? = null
        var emer_status_name: String? = null
        var remark: String? = null
    }
}