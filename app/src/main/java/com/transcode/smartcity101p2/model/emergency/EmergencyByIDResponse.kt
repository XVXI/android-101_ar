package com.transcode.smartcity101p2.model.emergency

class EmergencyByIDResponse {
    var code: String? = null
    var data = EmergencyByIDResponseData()

    class EmergencyByIDResponseData {
        var emer_id: String? = null
        var citizen_id: String? = null
        var emer_detail: String? = null
        var emer_by: String? = null
        var emer_lat: String? = null
        var emer_lng: String? = null
        var emer_status_id: String? = null
        var emer_type_id: String? = null
        var emer_tel: String? = null
        var create_datetime: String? = null
        var img = arrayListOf<EmergencyByIDResponseImg>()
        var dialog = arrayListOf<EmergencyByIDResponseDialog>()
        var rate: String? = null
        var comment: String? = null
        var emer_code: String? = null
    }

    class EmergencyByIDResponseImg {
        var emer_img_id: String? = null
        var emer_id: String? = null
        var img_url: String? = null
    }

    class EmergencyByIDResponseDialog {
        var emer_dialog_id: String? = null
        var emer_id: String? = null
        var dialog_msg: String? = null
        var create_datetime: String? = null
        var officer: Officer? = null
        var citizen: Citizen? = null
    }

    class Officer {
        var officer_id: String? = null
        var fname: String? = null
        var lname: String? = null
        var tel: String? = null
        var province: String? = null
        var amphue: String? = null
    }

    class Citizen {
        var citizen_id: String? = null
        var fname: String? = null
        var lname: String? = null
        var tel: String? = null
        var province: String? = null
        var amphue: String? = null
    }
}