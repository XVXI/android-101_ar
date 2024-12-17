package com.transcode.smartcity101p2.contract

interface ChangePasswordFragmentContract {
    interface View {
        fun showError(message: String)
        fun changePasswordSuccess()
    }

    interface Presenter {
        fun changePassword(email: String, password: String, newpassword: String, token: String)
    }
}