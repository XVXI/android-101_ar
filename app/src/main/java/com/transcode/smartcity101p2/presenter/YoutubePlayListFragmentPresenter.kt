package com.transcode.smartcity101p2.presenter

import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.YoutubePlayListFragmentContract
import com.transcode.smartcity101p2.model.YoutubePlayListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YoutubePlayListFragmentPresenter(var view: YoutubePlayListFragmentContract.View) : YoutubePlayListFragmentContract.Presenter {
    override fun requestYoutubePlayList(part: String, maxResults: String, playlistId: String, key: String) {
        val callbacks = object : Callback<YoutubePlayListResponse> {
            override fun onResponse(call: Call<YoutubePlayListResponse>?, response: Response<YoutubePlayListResponse>?) {
                response?.body()?.let {
                    view.updateList(it)
                } ?: kotlin.run {
                    view.requestFail("fail")
                }
            }

            override fun onFailure(call: Call<YoutubePlayListResponse>?, t: Throwable?) {
                view.requestFail("fail")
            }
        }

        ApiRequest.INSTANCE.requestYoutubePlayList(callbacks, part, maxResults, playlistId, key)
    }
}