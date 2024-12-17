package com.transcode.smartcity101p2.model

class RegisterRequest(var username: String, var password: String, var email: String,
                      var token: String?, var city_id: String, var citizen_id: String,
                      var fb_id: String?, var is_fb: String?,
                      var fname: String, var lname: String)