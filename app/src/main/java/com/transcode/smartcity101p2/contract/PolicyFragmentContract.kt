package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.PolicyResponse

interface PolicyFragmentContract {
    interface View {
        fun updateView(res: PolicyResponse)
    }

    interface Presenter {
        fun getPolicy()
    }
}