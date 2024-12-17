package com.transcode.smartcity101p2.model.complain

class CreateComplainRequest(var citizen_id: String,
                            var complain_detail: String,
                            var complain_by: String,
                            var complain_lat: String,
                            var complain_lng: String,
                            var complain_status_id: String,
                            var complain_type_id: String,
                            var city_id: String,
                            var complain_tel: String,
                            var file_extension: String,
                            var remark: String,
                            var img: Array<String?>)