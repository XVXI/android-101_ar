package com.transcode.smartcity101p2.presenter

import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.NewsDetailFragmentContract
import com.transcode.smartcity101p2.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsDetailFragmentPresenter(var view: NewsDetailFragmentContract.View) : NewsDetailFragmentContract.Presenter {
    override fun addComment(token: String, news_id: String, comment: String, rate: String, citizen_id: String) {
        val callbacks = object : Callback<AddCommentResponse> {
            override fun onResponse(call: Call<AddCommentResponse>?, response: Response<AddCommentResponse>?) {
                response?.let {
                    val res = it.body()
                    if (res?.code == "1" || res?.code == "200") {
                        getComment(token, news_id)
                    } else {
                        view.showError(res?.message.toString())
                    }
                } ?: kotlin.run {
                    view.showError(Const.MESSAGE_ERROR)
                }
            }

            override fun onFailure(call: Call<AddCommentResponse>?, t: Throwable?) {
                view.showError(Const.MESSAGE_ERROR)
            }
        }
        val request = AddCommentRequest(token, news_id, comment, rate, citizen_id)
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestAddComment(callbacks, request, bearer)
    }

    override fun getComment(token: String, news_id: String) {
        val callbacks = object : Callback<AllNewsCommentResponse> {
            override fun onResponse(call: Call<AllNewsCommentResponse>?, response: Response<AllNewsCommentResponse>?) {
                response?.body()?.let {
                    view.updateView(it.data)
                } ?: kotlin.run {
                    view.updateView(arrayListOf())
                }
            }

            override fun onFailure(call: Call<AllNewsCommentResponse>?, t: Throwable?) {
                view.updateView(arrayListOf())
            }
        }
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val city_id = loginResponse.authority_info?.CityId ?: "6"
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        val request = GetNewsCommentRequest(token, news_id, city_id)
        ApiRequest.INSTANCE.requestGetComment(callbacks, request, bearer)
    }

    override fun getNewsFile(token: String, news_id: String) {
        val callbacks = object : Callback<NewsFileResponse> {
            override fun onResponse(call: Call<NewsFileResponse>?, response: Response<NewsFileResponse>?) {
                response?.body()?.let {
                    view.updateNewsFile(it.data)
                } ?: kotlin.run {
                    view.updateNewsFile(arrayListOf())
                }
            }

            override fun onFailure(call: Call<NewsFileResponse>?, t: Throwable?) {
                view.updateNewsFile(arrayListOf())
            }
        }
        val request = NewsFileRequest(token, news_id)
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val bearer = "Bearer " + (loginResponse.server?.token ?: "")
        ApiRequest.INSTANCE.requestNewsFile(callbacks, request, bearer)
    }
}