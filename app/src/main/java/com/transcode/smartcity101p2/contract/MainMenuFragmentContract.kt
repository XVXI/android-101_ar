package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.LoginResponse

interface MainMenuFragmentContract {
    interface View {
        fun onLoginSuccess(loginResponse: LoginResponse)
        fun onLoginFail(message: String)
        fun onUpdateDataComplete()
        fun onGetCitizenInfoSuccess(data: CitizenInfoResponse.CitizenInfoResponseData)
        fun showError(message: String)
    }

    interface Presenter {
        fun requestLoginGuest(udid: String)
        fun getCitizenInfo()
    }
}