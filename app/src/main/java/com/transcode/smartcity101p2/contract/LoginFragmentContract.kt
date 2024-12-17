package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.LoginResponse

interface LoginFragmentContract {
    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun onLoginSuccess(res: LoginResponse)
        fun onLoginFail(message: String)
    }

    interface Presenter {
        fun requestLogin(user: String, password: String, city_id: String)
    }
}