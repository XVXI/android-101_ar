package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueSlotResponse

interface SelectTimeFragmentContract {
    interface View {
        fun showError(message: String)
        fun bookSuccess(myQueueResponse: MyQueueResponse)
        fun getQueueSlotSuccess(list:ArrayList<QueueSlotResponse.QueueSlotData>)
    }

    interface Presenter {
        fun bookQueue(queue_id: String, choose_datatime: String, citizen_id: String, queue_slot_id: String)
        fun getQueueSlot(queue_id: String, choose_datetime: String)
    }
}