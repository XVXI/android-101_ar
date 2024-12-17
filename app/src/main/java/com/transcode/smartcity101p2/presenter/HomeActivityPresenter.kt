package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.HomeActivityContract
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.utils.AppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivityPresenter(var view: HomeActivityContract.View) : HomeActivityContract.Presenter {

    private val milsec = 1000
    private val minit = milsec * 60
    private val hr = minit * 60
    private val day = hr * 24

    override fun getNews(news_cat_id: String?, limit: Int, offset: Int) {
        val callbacks = object : Callback<AllNewsResponse> {
            override fun onResponse(call: Call<AllNewsResponse>?, response: Response<AllNewsResponse>?) {
                response?.body()?.data?.let {
                    val list = arrayListOf<NewsResponse>()
                    for (data in it) {
                        list.add(data)
                        if (list.size >= 5) {
                            break
                        }
                    }
                    view.updateNewsView(list)
                } ?: kotlin.run {
                    view.showError("")
                }
            }

            override fun onFailure(call: Call<AllNewsResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        var cityid = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestNews(callbacks, NewsRequest(cityid, news_cat_id, token, limit.toString(), offset.toString()), bearer)
    }

    override fun getNewsImage(news: NewsResponse) {
        var news_id = news.NewsId ?: ""
        val callbacks = object : Callback<NewsImgResponse> {
            override fun onResponse(call: Call<NewsImgResponse>?, response: Response<NewsImgResponse>?) {
                response?.body()?.data?.let {
                    view.updateImageList(news_id, it)
                } ?: kotlin.run {
                    view.updateImageList(news_id, arrayListOf())
                }
            }

            override fun onFailure(call: Call<NewsImgResponse>?, t: Throwable?) {
                view.updateImageList(news_id, arrayListOf())
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val token = loginResponse.authority_info?.getAllToken() ?: ""
        ApiRequest.INSTANCE.requestNewsImage(callbacks, NewsImgRequest(token, news_id), bearer)
    }

    override fun getCitizenInfo() {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val callbacks = object : Callback<CitizenInfoResponse> {
            override fun onResponse(call: Call<CitizenInfoResponse>?, response: Response<CitizenInfoResponse>?) {
                response?.body()?.data?.let {
                    it.CitizenId?.apply {
                        view.updateCitizenInfo(it)
                    }
                }
            }

            override fun onFailure(call: Call<CitizenInfoResponse>?, t: Throwable?) {
//                Log.e("re", "re")
            }
        }
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestCitizenInfo(callbacks, CitizenInfoRequest(citizen_id, loginResponse.authority_info?.getAllToken()
                ?: ""), bearer)
    }

    override fun getMyQueue() {
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
                    view.updateMyqueue(myList)
                }
            }

            override fun onFailure(call: Call<ArrayList<MyQueueResponse>>?, t: Throwable?) {

            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        var citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestMyQueueList(callbacks, citizen_id)
    }
}