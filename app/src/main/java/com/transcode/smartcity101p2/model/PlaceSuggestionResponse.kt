package com.transcode.smartcity101p2.model

class PlaceSuggestionResponse {
    var code: String? = null
    var data = arrayListOf<PlaceSuggestionResponseData>()

    class PlaceSuggestionResponseData {
        var place_id: String? = null
        var place_name: String? = null
        var lat: String? = null
        var lng: String? = null
        var rating: String? = null
        var type_id: String? = null
        var type_name: String? = null
        var place_img_id: String? = null
        var image_path: String? = null
    }
}