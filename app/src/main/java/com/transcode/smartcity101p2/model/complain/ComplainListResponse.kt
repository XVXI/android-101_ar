package com.transcode.smartcity101p2.model.complain

class ComplainListResponse {
    var code: String? = null
    var data = arrayListOf<ComplainListResponseData>()

    class ComplainListResponseData {
        var complain_id: String? = null
        var citizen_id: String? = null
        var complain_detail: String? = null
        var complain_lat: String? = null
        var complain_lng: String? = null
        var complain_type_id: String? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
        var complain_status_id: String? = null
        var rate: String? = null
        var comment: String? = null
        var rate_datetime: String? = null
        var complain_by: String? = null
        var complain_tel: String? = null
        var complain_status_name: String? = null
        var remark: String? = null
    }
}