package com.transcode.smartcity101p2.model

class PlanListResponse {
    var code: String? = null
    var message: String? = null
    var data: ArrayList<PlanListResponseData> = arrayListOf()

    class PlanListResponseData {
        var PlanId: String? = null
        var Day: String? = null
        var PlanName: String? = null
        var Remark: String? = null
        var StatusId: String? = null
        var CreateDatetime: String? = null
        var UpdateDatetime: String? = null
        var img: String? = null
    }
}