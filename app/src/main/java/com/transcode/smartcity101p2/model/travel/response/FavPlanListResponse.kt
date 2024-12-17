package com.transcode.smartcity101p2.model.travel.response

class FavPlanListResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<FavPlanListResponseData>()

    class FavPlanListResponseData {
        var fav_id: String? = null
        var citizen_id: String? = null
        var plan_id: String? = null
        var create_datetime: String? = null
        var update_datetime: String? = null
        var plan_code: String? = null
        var plan_name: String? = null
        var day: String? = null
        var remark: String? = null
        var status_id: String? = null
        var plan_type: String? = null
        var Is_active: String? = null
        var city_id: String? = null
        var img: String? = null
        var plan_name_eng: String? = null
        var plan_name_cn: String? = null
        var remark_eng: String? = null
        var remark_cn: String? = null
    }
}