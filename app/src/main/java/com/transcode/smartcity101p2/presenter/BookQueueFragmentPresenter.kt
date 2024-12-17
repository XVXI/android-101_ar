package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.BookQueueFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.GetBookQueueNowResponse
import com.transcode.smartcity101p2.utils.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookQueueFragmentPresenter(var view: BookQueueFragmentContract.View) : BookQueueFragmentContract.Presenter {
    override fun bookQueue(queue_type: String, queue_id: String, choose_datatime: String, citizen_id: String, lat: String, lng: String, queue_slot_id: String, form_id: String) {
        val dateTimeMill = AppUtils.dateStringToMillis(choose_datatime, arrayOf(AppUtils.formateDate7))
        val new_date = AppUtils.getDateString(AppUtils.formateDate2, dateTimeMill)

        val callbacks = object : Callback<GetBookQueueNowResponse> {
            override fun onResponse(call: Call<GetBookQueueNowResponse>?, response: Response<GetBookQueueNowResponse>?) {
                response?.body()?.let {
                    if (it.code == "2201" || it.code == "200" || it.code == "1") {
                        view.bookSuccess(it.data[0])
                    } else {
                        view.showError(it.message.toString())
                    }
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<GetBookQueueNowResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_BOOK_ERROR)
            }
        }

        if (queue_type == "2") {
            ApiRequest.INSTANCE.requestBook(callbacks, queue_id, new_date, citizen_id, queue_slot_id, form_id)
        } else {
            ApiRequest.INSTANCE.requestBookWithLatLng(callbacks, queue_id, new_date, citizen_id, lat, lng, queue_slot_id, form_id)
        }
    }
}