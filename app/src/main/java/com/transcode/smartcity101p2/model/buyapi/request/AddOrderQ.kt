package com.transcode.smartcity101p2.model.buyapi.request

class AddOrderQ(var citizen_id: String,
                var geo_location: String,
                var delivery_name: String,
                var delivery_address: String,
                var delivery_tel: String,
                var details: ArrayList<AddOrderQDetail>)