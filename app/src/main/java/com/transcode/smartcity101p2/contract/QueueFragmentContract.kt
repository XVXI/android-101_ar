package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.MyQueueResponse

interface QueueFragmentContract {
    interface View {
        fun updateList(res: ArrayList<Any>)
        fun showError(message: String)
        fun setAlert(queue: MyQueueResponse)
    }

    interface Presenter {
        fun getQueueList()
    }
}