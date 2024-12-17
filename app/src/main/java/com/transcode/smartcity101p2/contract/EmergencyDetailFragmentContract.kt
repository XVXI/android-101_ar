package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.emergency.EmergencyByIDResponse

interface EmergencyDetailFragmentContract {
    interface View {
        fun updateView(data: EmergencyByIDResponse.EmergencyByIDResponseData)
        fun createDialogSuccess()
    }

    interface Presenter {
        fun getDetailByID(emer_id: String)
        fun createDialog(emer_id: String, message: String)
        fun updateEmerDialog(emer_id: String, emer_dialog_id: ArrayList<String>)
    }
}