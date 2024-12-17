package com.transcode.smartcity101p2.model

class PolicyResponse {

    var code: String? = null
    var message: String? = null
    var data: InnerPolicyResponse? = null

    class InnerPolicyResponse {
        var PolicyId: String? = null
        var PolicyDetail: String? = null
    }
}