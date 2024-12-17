package com.transcode.smartcity101p2.contract

interface MarketAddressDetailActivityContract {
    interface View {
        fun updateSuccess()
        fun updateError()
    }

    interface Presenter {
        fun deleteAddress(address_id: String)
        fun addAddress(name: String, address: String, tel: String)
        fun updateAddress(citizen_address_id: String, name: String, address: String, tel: String)
    }
}