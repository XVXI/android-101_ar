package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.QueueFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueListResponse
import com.transcode.smartcity101p2.utils.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QueueFragmentPresenter(var view: QueueFragmentContract.View) : QueueFragmentContract.Presenter {

    private val milsec = 1000
    private val minit = milsec * 60
    private val hr = minit * 60
    private val day = hr * 24

    var list = arrayListOf<Any>()
    var allload = true

    override fun getQueueList() {
        if (allload) {
            allload = false
            list.clear()
            getMyQueue()
        }
    }

    private fun getMyQueue() {
        val callbacks = object : Callback<ArrayList<MyQueueResponse>> {
            override fun onResponse(call: Call<ArrayList<MyQueueResponse>>?, response: Response<ArrayList<MyQueueResponse>>?) {
                response?.body()?.let {
                    val myList = arrayListOf<MyQueueResponse>()
                    for (item in it) {
                        val mill1 = AppUtils.dateStringToMillis(item.ChooseDatetime.toString(), arrayOf(AppUtils.formateDate2))
                        val date = AppUtils.getDateString(AppUtils.formateDate3, mill1)
                        val max_select_date_timeminll = AppUtils.dateStringToMillis(date, arrayOf(AppUtils.formateDate3)) + day
                        if (System.currentTimeMillis() < max_select_date_timeminll) {
                            myList.add(item)
                        }
                    }
                    if (myList.size > 0) {
                        list.add(convertHeader("-3"))
                        list.addAll(myList)
                    }

                    for (queue in it) {
                        if (Hawk.get<MyQueueResponse>(queue.QueueId.toString()) == null) {
                            val selectDateMills = AppUtils.dateStringToMillis(queue.ChooseDatetime.toString(), arrayOf(AppUtils.formateDate2))
                            val currentTime = System.currentTimeMillis()

                            //check time
                            if (selectDateMills - currentTime > hr) {
                                Hawk.put(queue.QueueId.toString(), queue)
                                //add to alarm
                                view.setAlert(queue)
                            }
                        }
                    }
                }
                getAllQueue()
            }

            override fun onFailure(call: Call<ArrayList<MyQueueResponse>>?, t: Throwable?) {
                getAllQueue()
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestMyQueueList(callbacks, citizen_id)
    }

    private fun mapQueueList(list: ArrayList<QueueListResponse>): HashMap<String, ArrayList<QueueListResponse>> {
        val hashMap = HashMap<String, ArrayList<QueueListResponse>>()
        val list1 = arrayListOf<QueueListResponse>()
        val list2 = arrayListOf<QueueListResponse>()
        val list3 = arrayListOf<QueueListResponse>()
        for (i in list) {
            if (i.QueueType == "1") {
                list1.add(i)
            } else if (i.QueueType == "2") {
                list2.add(i)
            } else if (i.QueueType == "3") {
                list3.add(i)
            }
        }
        hashMap["1"] = list1
        hashMap["2"] = list2
        hashMap["3"] = list3
        return hashMap
    }

    private fun getAllQueue() {
        val callbacks = object : Callback<ArrayList<QueueListResponse>> {
            override fun onResponse(call: Call<ArrayList<QueueListResponse>>?, response: Response<ArrayList<QueueListResponse>>?) {
                response?.body()?.let {
                    if (list.isEmpty()) {
                        val mapData = mapQueueList(it)
                        for (m in mapData) {
                            if (!m.value.isEmpty()) {
                                list.add(convertHeader(m.key))
                                list.addAll(m.value)
                            }
                        }

                    } else {
                        val mapData = mapQueueList(it)
                        val tmp = arrayListOf<QueueListResponse>()
                        for (m in mapData) {
                            if (!m.value.isEmpty()) {
                                for (data in m.value) {
                                    var inList = false
                                    for (i in list) {
                                        if (i is MyQueueResponse) {
                                            if (i.QueueId == data.QueueId) {
                                                inList = true
                                            }
                                        }
                                    }
                                    if (!inList) {
                                        tmp.add(data)
                                    }
                                }
                            }
                            if (!tmp.isEmpty()) {
                                list.add(convertHeader(m.key))
                                list.addAll(tmp)
                                tmp.clear()
                            }
                        }
                    }

                    allload = true
                    view.updateList(list)
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<ArrayList<QueueListResponse>>?, t: Throwable?) {
                allload = true
                view.showError(Const.MESSAGE_ERROR)
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        var city_id = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestQueueList(callbacks, city_id)
    }

    private fun convertHeader(type: String): String {
        return when (type) {
            "1" -> "จองทันที"
            "2" -> "จองล่วงหน้า"
            "3" -> "นอกสถานที่"
            else -> "คิวของคุณ"
        }
    }
}