package com.transcode.smartcity101p2.model.complain

class ComplainByIDResponse {
    var code: String? = null
    var data = ComplainByIDResponseData()

    class ComplainByIDResponseData {
        var complain_id: String? = null
        var citizen_id: String? = null
        var complain_detail: String? = null
        var complain_by: String? = null
        var complain_lat: String? = null
        var complain_lng: String? = null
        var complain_status_id: String? = null
        var complain_type_id: String? = null
        var complain_tel: String? = null
        var create_datetime: String? = null
        var img = arrayListOf<ComplainByIDResponseImg>()
        var dialog = arrayListOf<ComplainByIDResponseDialog>()
        var rate: String? = null
        var comment: String? = null
        var complain_code: String? = null
    }

    class ComplainByIDResponseImg {
        var complain_img_id: String? = null
        var complain_id: String? = null
        var img_url: String? = null
    }

    class ComplainByIDResponseDialog {
        var complain_dialog_id: String? = null
        var complain_id: String? = null
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