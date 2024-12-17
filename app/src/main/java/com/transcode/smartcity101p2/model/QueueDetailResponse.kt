package com.transcode.smartcity101p2.model

class QueueDetailResponse {
    var QueueId: String? = null
    var QueueName: String? = null
    var QueueDetail: String? = null
    var QueueLat: String? = null
    var QueueLng: String? = null
    var TotalChannel: String? = null
    var DepartSubId: String? = null
    var QueueType: String? = null
    var IsActive: String? = null
    var PreQueueCode: String? = null
    var CityId: String? = null
    var QueueImg: ArrayList<QueueImgObj>? = null
    var QueueFile: ArrayList<QueueFileObj>? = null
    var FormURL: String? = null

    class QueueImgObj {
        var QueueImgId: String? = null
        var ImgURL: String? = null
    }

    class QueueFileObj {
        var QueueFileId: String? = null
        var FileURL: String? = null
    }
}