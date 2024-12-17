package com.transcode.smartcity101p2.model

class LoginResponse {
    var code: String? = null
    var message: String? = null
    var authority_info: AuthorityInfo? = null
    var server: ServerInfo? = null

    class AuthorityInfo {
        var CZAccountId: String? = null
        var Email: String? = null
        var CityId: String? = null
        var CityName: String? = null
        var LogoUrl: String? = null
        var CitizenId: String? = null
        var CreateDateTime: String? = null
        var FbId: String? = null
        var IsFb: String? = null
        var Token: String? = null
        var Tokens: String? = null
        var BgColor: String? = null
        var FName: String? = null
        var LName: String? = null
        var ArticleNewsCatId: String? = null
        var AwardNewsCatId: String? = null
        var YoutubeList: String? = null

        fun getAllToken(): String? {
            return when {
                Token != null -> Token
                Tokens != null -> Tokens
                else -> null
            }
        }
    }

    class ServerInfo {
        var now: String? = null
        var token_type: String? = null
        var token: String? = null
    }
}