package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueDetailResponse

interface QueueDetailFragmentContract {
    interface View {
        fun updateView(queueDetailResponse: QueueDetailResponse)
        fun showError(message: String)
        fun bookSuccess(myQueueResponse: MyQueueResponse)
    }

    interface Presenter {
        fun bookNow(queue_id: String, citizen_id: String)
        fun getQueueDetail(queue_id: String)
    }
}