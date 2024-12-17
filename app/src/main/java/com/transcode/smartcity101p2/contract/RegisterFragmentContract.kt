package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.LoginResponse

interface RegisterFragmentContract {
    interface View {
        fun updateFBRegister(firstName: String, lastName: String, emailString: String)
        fun showError(message:String)
        fun onLoginSuccess(res: LoginResponse)
        fun onLoginFail(message: String)
        fun registerSuccess()
    }

    interface Presenter {
        fun getFacebookDetail()
    }
}