package com.transcode.smartcity101p2.model

class PlanDetailResponse {

    var code: String? = null
    var message: String? = null
    var data = arrayListOf<PlanDetailResponseData>()

    class PlanDetailResponseData {
        var PlanId: String? = null
        var Day: String? = null
        var PlanName: String? = null
        var Remark: String? = null
        var StatusId: String? = null
        var CreateDatetime: String? = null
        var UpdateDatetime: String? = null
        var Details = arrayListOf<DataDetails>()
    }

    class DataDetails {
        var PlanId: String? = null
        var DetailId: String? = null
        var PlaceId: String? = null
        var Sequence: String? = null
        var StartTracalTime: String? = null
        var TravelMins: String? = null
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
        var day_no: String? = null
        var img = arrayListOf<Image>()
        var StoreOpens = arrayListOf<StoreOpens>()
        var distance: String? = null
    }

    class StoreOpens {
        var StoreopenId: String? = null
        var Daytype: String? = null
        var OpenTime: String? = null
        var CloseTime: String? = null
    }

    class Image {
        var place_img_id: String? = null
        var image_path: String? = null
    }
}