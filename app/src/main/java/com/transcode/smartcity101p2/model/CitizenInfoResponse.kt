package com.transcode.smartcity101p2.model

class CitizenInfoResponse {
    var code: String? = null
    var message: String? = null
    var data = CitizenInfoResponseData()

    class CitizenInfoResponseData {
        var CitizenId: String? = null
        var FName: String? = null
        var LName: String? = null
        var Email: String? = null
        var Tel: String? = null
        var ProvinceId: String? = null
        var ProvinceName: String? = null
        var AmphueId: String? = null
        var AmphueName: String? = null
        var TambonId: String? = null
        var TambonName: String? = null
        var Address: String? = null
        var ZipCode: String? = null
        var UpDateDateTime: String? = null
        var CitizenImg: String? = null
        var IdCard: String = ""
    }
}