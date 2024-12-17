package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.QueueDetailResponse

interface TicketDetailFragmentContract {
    interface View {
        fun updateView(queueDetailResponse: QueueDetailResponse)
        fun showError(message: String)
        fun cancelSuccess()
    }

    interface Presenter {
        fun getQueueDetail(queue_id: String)
        fun cancelQueueCheckin(queue_checkin_id: String)
    }
}