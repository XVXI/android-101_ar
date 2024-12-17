package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyQueueResponse

interface BookQueueFragmentContract {
    interface View {
        fun showError(message: String)
        fun bookSuccess(myQueueResponse: MyQueueResponse)
    }

    interface Presenter {
        fun bookQueue(queue_type: String, queue_id: String, choose_datatime: String, citizen_id: String, lat: String, lng: String, queue_slot_id: String, form_id: String)
    }
}