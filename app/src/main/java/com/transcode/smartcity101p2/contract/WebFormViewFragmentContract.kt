package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyQueueResponse

interface WebFormViewFragmentContract {
    interface View {
        fun showError(message: String)
        fun bookSuccess(myQueueResponse: MyQueueResponse)
    }

    interface Presenter {
        fun bookNow(queue_id: String, citizen_id: String, form_id: String)
    }
}