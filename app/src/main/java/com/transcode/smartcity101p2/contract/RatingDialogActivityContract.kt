package com.transcode.smartcity101p2.contract

interface RatingDialogActivityContract {
    interface View {
        fun editSuccess()
        fun showError(message: String)
    }

    interface Presenter {
        fun editQueueCheckin(queue_checkin_id: String, rate: String, comment: String)
    }
}