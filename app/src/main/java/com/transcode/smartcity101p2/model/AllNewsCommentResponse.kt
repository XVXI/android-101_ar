package com.transcode.smartcity101p2.model

class AllNewsCommentResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<AllNewsCommentResponseItem>()

    class AllNewsCommentResponseItem {
        var NewsCommentId: String? = null
        var NewsId: String? = null
        var Comment: String? = null
        var Rate: String? = null
        var CitizenId: String? = null
        var CreateDateTime: String? = null
        var FName: String? = null
        var LName: String? = null
        var CitizenImg: String? = ""
        var Day: String? = ""
        var Time: String? = ""
    }
}