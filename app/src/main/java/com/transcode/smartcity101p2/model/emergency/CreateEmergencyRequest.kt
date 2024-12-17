package com.transcode.smartcity101p2.model.emergency

class CreateEmergencyRequest(var citizen_id: String,
                             var emer_detail: String,
                             var emer_by: String,
                             var emer_lat: String,
                             var emer_lng: String,
                             var emer_status_id: String,
                             var emer_type_id: String,
                             var city_id: String,
                             var emer_tel: String,
                             var file_extension: String,
                             var remark: String,
                             var img: Array<String?>)