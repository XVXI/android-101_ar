package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.QueueSlotResponse

interface SelectDateFragmentContract {
    interface View {
        fun updateView(list: ArrayList<HashMap<String, Any>>)
        fun showError(message: String)
        fun getQueueSlotSuccess(list: ArrayList<QueueSlotResponse.QueueSlotData>, groupPosition: Int)
        fun editChooseDatetimeSuccess()
    }

    interface Presenter {
        fun calculateDayList(queue_type: String)
        fun getQueueSlot(groupPosition: Int, queue_id: String, choose_datetime: String)
        fun editChooseDatetime(queue_checkin_id: String, choose_datetime: String, queue_slot_id: String)
    }
}