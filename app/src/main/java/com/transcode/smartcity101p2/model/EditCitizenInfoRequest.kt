package com.transcode.smartcity101p2.model

class EditCitizenInfoRequest(var id_card: String,
                             var citizen_id: String,
                             var fname: String,
                             var lname: String,
                             var tel: String,
                             var province_id: String,
                             var amphue_id: String,
                             var tambon_id: String,
                             var address: String,
                             var zipcode: String,
                             var token: String)