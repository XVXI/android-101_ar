package com.transcode.smartcity101p2.model

class Const {
    companion object {
        const val KEY_CITY = "city"
        const val KEY_LOGIN_DATA = "login_data"
        const val KEY_NEED_LOGIN = "need_login"

        const val SSL_CERTIFICATE_ERROR = "SSL Certificate error."
        const val AUTHORITY_NOT_TRUSTED = "The certificate authority is not trusted."
        const val CERTIFICATE_HAS_EXPIRED = "The certificate has expired."
        const val CERTIFICATE_HOSTNAME_MISMATCH = "The certificate Hostname mismatch."
        const val CERTIFICATE_IS_NOT_YET_VALID = "The certificate is not yet valid."
        const val POST_FIX_MESSAGE = " Do you want to continue anyway?"
        const val TITLE_SSL_ERROR = "SSL Certificate Error"
        const val TITLE_BUTTON_CONTINUE = "continue"
        const val TITLE_BUTTON_CANCEL = "cancel"

        const val MESSAGE_ERROR = "ไม่สามารถดำเนินการได้สำเร็จกรุณาลองใหม่อีกครั้ง"
        const val MESSAGE_BOOK_ERROR = "กรุณาจองคิวใหม่ในช่วงเวลาอื่น"
        const val MESSAGE_SUCCESS = "ดำเนินการสำเร็จ"

        const val MESSAGE_NO_QUEUE_SLOT = "ไม่มีบริการในวันที่เลือก"

        const val REQUEST_CODE_BOOK_QUEUE = 1423

        const val WISHLIST = "WishList"
        const val CARTLIST = "CartList"
        const val CART_COUNT = "CART_COUNT"

        const val RC_SIGN_IN = 7707

        const val BROADCAST_COMPLAIN = "com.transcode.smartcity101p2.firebase.emergency"
        const val BROADCAST_EMERGENCY = "com.transcode.smartcity101p2.firebase.complain"
        const val BROADCAST_TOGGLE_NOTIFICATION = "com.transcode.smartcity101p2.firebase.broadcast_notification"

        const val EMER_CHAT_DB = "EMER_CHAT_DB"
        const val COMPLAIN_CHAT_DB = "COMPLAIN_CHAT_DB"

        const val MAP_STYLE_URI = "https://dev.maps.transcodeglobal.com/styles/osm-bright/style.json"

        const val STATIC_STORE = "https://retsm0storage0market.blob.core.windows.net/retsm0storage0public/"
    }
}