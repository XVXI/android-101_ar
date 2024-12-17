package com.transcode.smartcity101p2.model.travel.response

class FavPlaceListResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<FavPlaceListResponseData>()

    class FavPlaceListResponseData {
        var fav_id: String? = null
        var place_id: String? = null
        var citizen_id: String? = null
        var place_name: String? = null
        var place_name_cn: String? = null
        var place_name_eng: String? = null
        var img = arrayListOf<FavPlaceListResponseImg>()
    }

    class FavPlaceListResponseImg {
        var place_id: String? = null
        var image_path: String? = null
    }
}