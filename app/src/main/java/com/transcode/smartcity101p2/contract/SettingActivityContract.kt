package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CitizenInfoResponse

interface SettingActivityContract{
    interface View {
        fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData)
        fun onFail()
    }

    interface Presenter {
        fun getCitizenInfo()
    }
}