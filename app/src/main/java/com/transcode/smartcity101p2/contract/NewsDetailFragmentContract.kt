package com.transcode.smartcity101p2.contract

import com.transcode.smartcity101p2.model.AllNewsCommentResponse
import com.transcode.smartcity101p2.model.NewsFileResponse

interface NewsDetailFragmentContract {
    interface View {
        fun updateView(lits: ArrayList<AllNewsCommentResponse.AllNewsCommentResponseItem>)
        fun updateNewsFile(list: ArrayList<NewsFileResponse.NewsFileResponseData>)
        fun showError(message: String)
    }

    interface Presenter {
        fun addComment(token: String, news_id: String, comment: String, rate: String, citizen_id: String)
        fun getComment(token: String, news_id: String)
        fun getNewsFile(token: String, news_id: String)
    }
}