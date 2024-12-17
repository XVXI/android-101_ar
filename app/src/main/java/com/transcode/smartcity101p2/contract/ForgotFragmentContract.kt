package com.transcode.smartcity101p2.contract

interface ForgotFragmentContract {
    interface View {
        fun updateView()
        fun showError(message: String)
    }

    interface Presenter {
        fun forgotPassword(email: String)
    }
}