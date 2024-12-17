package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.complain.ComplainByIDResponse

interface ComplainDetailFragmentContract {
    interface View {
        fun updateView(data: ComplainByIDResponse.ComplainByIDResponseData)
        fun createDialogSuccess()
    }

    interface Presenter {
        fun getDetailByID(complain_id: String)
        fun createDialog(complain_id: String, message: String)
        fun updateComplainDialog(complain_id: String, complain_dialog_id: ArrayList<String>)
    }
}