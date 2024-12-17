package com.transcode.smartcity101p2.model

class ProvinceResponse {
    var code: String? = null
    var message: String? = null
    var data = arrayListOf<ProvinceResponseData>()

    class ProvinceResponseData {
        var province_id: String? = null
        var province_code: String? = null
        var province_name: String? = null
        var province_name_eng: String? = null

//        var ProvinceId: String? = province_id
//        var ProvinceCode: String? = province_code
//        var ProvinceName: String? = province_name
//        var ProvinceNameEng: String? = province_name_eng
        var GeoId: String? = null
    }
}