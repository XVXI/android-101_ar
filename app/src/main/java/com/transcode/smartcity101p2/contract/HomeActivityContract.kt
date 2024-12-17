package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.CitizenInfoResponse
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.NewsImgResponse
import com.transcode.smartcity101p2.model.NewsResponse

interface HomeActivityContract {
    interface View {
        fun updateNewsView(list: ArrayList<NewsResponse>)
        fun updateImageList(news_id: String, list: ArrayList<NewsImgResponse.ImageData>)
        fun showError(message: String)
        fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData)
        fun updateMyqueue(list: ArrayList<MyQueueResponse>)
    }

    interface Presenter {
        fun getNews(news_cat_id: String?, limit: Int, offset: Int)
        fun getNewsImage(news: NewsResponse)
        fun getCitizenInfo()
        fun getMyQueue()
    }
}