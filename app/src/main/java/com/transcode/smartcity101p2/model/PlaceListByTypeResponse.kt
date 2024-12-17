package com.transcode.smartcity101p2.model

class PlaceListByTypeResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<PlaceMaker>()


    class PlaceMaker {
        var PlaceId: String? = null
        var PlaceName: String? = null
        var Lat: String? = null
        var Lng: String? = null
        var Remark: String? = null
        var Rating: String? = null
        var ContractName: String? = null
        var ContractTel: String? = null
        var IsActive: String? = null
        var Type: String? = null
        var CreateDatetime: String? = null
        var UpdateDatetime: String? = null
        var CityId: String? = null
        var ImagePath: String? = null
    }
}