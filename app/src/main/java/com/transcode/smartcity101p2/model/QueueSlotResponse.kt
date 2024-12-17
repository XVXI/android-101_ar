package com.transcode.smartcity101p2.model

class QueueSlotResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<QueueSlotData>()

    class QueueSlotData {
        var QueueSlotId: String? = null
        var QueueId: String? = null
        var QueueFromTime: String? = null
        var QueueToTime: String? = null
        var QueueLimit: String? = null
        var DayInWeek: String? = null
        var CountCheckin: String? = null
        var selected = false
    }
}