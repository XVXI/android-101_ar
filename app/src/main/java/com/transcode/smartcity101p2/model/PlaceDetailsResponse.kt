package com.transcode.smartcity101p2.model

class PlaceDetailsResponse {
    var code: String? = null
    var data: PlaceDetailsResponseData? = null

    class PlaceDetailsResponseData {
        var place_id: String? = null
        var place_name: String? = null
        var lat: String? = null
        var lng: String? = null
        var remark: String? = null
        var rating: String? = null
        var contract_name: String? = null
        var contract_tel: String? = null
        var stamp_img: String? = null
        var stamp_img_bw: String? = null
        var type_id: String? = null
        var type_name: String? = null
        var icon_url: String? = null
        var province_id: String? = null
        var province_name: String? = null
        var province_name_eng: String? = null
        var amphue_id: String? = null
        var amphue_name: String? = null
        var amphue_name_eng: String? = null
        var tambon_id: String? = null
        var tambon_name: String? = null
        var tambon_name_eng: String? = null
        var is_suggest: String? = null
        var img = arrayListOf<Img>()
        var comment = arrayListOf<Comment>()
        var store_open = arrayListOf<StoreOpens>()
        var is_ar: String? = null
        var AR_total: String? = null
    }

    class StoreOpens {
        var StoreopenId: String? = null
        var Daytype: String? = null
        var OpenTime: String? = null
        var CloseTime: String? = null
    }

    class Img {
        var place_img_id: String? = null
        var image_path: String? = null
    }

    class Comment {
        var comment_id: String? = null
        var comment: String? = null
        var rating: String? = null
        var create_datetime: String? = null
        var citizen_id: String? = null
        var fname: String? = null
        var lname: String? = null
    }
}