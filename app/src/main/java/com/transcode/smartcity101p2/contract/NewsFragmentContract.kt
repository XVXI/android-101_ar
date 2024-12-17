package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.NewsImgResponse
import com.transcode.smartcity101p2.model.NewsResponse

interface NewsFragmentContract {
    interface View {
        fun updateView(list: ArrayList<NewsResponse>)
        fun updateImageList(news_id: String, list: ArrayList<NewsImgResponse.ImageData>)
        fun showError(message: String)
    }

    interface Presenter {
        fun getNews(news_cat_id: String?, limit: Int, offset: Int)
        fun getNewsImage(news: NewsResponse)
    }
}