package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.NewsFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragmentPresenter(var view: NewsFragmentContract.View) : NewsFragmentContract.Presenter {
    override fun getNews(news_cat_id: String?, limit: Int, offset: Int) {
        val callbacks = object : Callback<AllNewsResponse> {
            override fun onResponse(call: Call<AllNewsResponse>?, response: Response<AllNewsResponse>?) {
                response?.body()?.data?.let {
                    view.updateView(it)
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
}