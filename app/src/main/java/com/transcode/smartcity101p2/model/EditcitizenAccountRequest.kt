package com.transcode.smartcity101p2.model

class EditcitizenAccountRequest(var email: String,
                                var token: String,
                                var city_id: String,
                                var citizen_id: String,
                                var data: String?,
                                var firebase_client_id: String)